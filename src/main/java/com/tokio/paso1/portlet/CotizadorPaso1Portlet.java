package com.tokio.paso1.portlet;

import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.tokio.cotizadorModular.Bean.CotizadorDataResponse;
import com.tokio.cotizadorModular.Bean.InfoCotizacion;
import com.tokio.cotizadorModular.Bean.ListaRegistro;
import com.tokio.cotizadorModular.Bean.Persona;
import com.tokio.cotizadorModular.Bean.Registro;
import com.tokio.cotizadorModular.Bean.SimpleResponse;
import com.tokio.cotizadorModular.Constants.CotizadorModularServiceKey;
import com.tokio.cotizadorModular.Enum.ModoCotizacion;
import com.tokio.cotizadorModular.Enum.TipoCotizacion;
import com.tokio.cotizadorModular.Enum.TipoPersona;
import com.tokio.cotizadorModular.Exception.CotizadorModularException;
import com.tokio.cotizadorModular.Interface.CotizadorGenerico;
import com.tokio.cotizadorModular.Interface.CotizadorPaso1;
import com.tokio.cotizadorModular.Util.CotizadorModularUtil;
import com.tokio.paso1.constants.CotizadorPaso1PortletKeys;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author jonathanfviverosmoreno
 */
@Component(immediate = true, property = { "com.liferay.portlet.display-category=category.sample",
		"com.liferay.portlet.instanceable=true",
		"javax.portlet.display-name=CotizadorPaso1Portlet Portlet",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.name=" + CotizadorPaso1PortletKeys.PORTLET_NAME,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.requires-namespaced-parameters=false",
		"com.liferay.portlet.private-request-attributes=false" }, service = Portlet.class)
public class CotizadorPaso1Portlet extends MVCPortlet {

	@Reference
	CotizadorPaso1 _CMServicesP1;
	@Reference
	CotizadorGenerico _CMServicesGenerico;

	InfoCotizacion infCotizacion;
	User user;
	int idPerfilUser;

	@Override
	public void render(RenderRequest renderRequest, RenderResponse renderResponse)
			throws PortletException, IOException {

		// generaURL();
//		auxPruebas();
		
		llenaInfoCotizacion(renderRequest);
		cargaCatalogos(renderRequest);
		generaFechas(renderRequest);
		seleccionaModo(renderRequest, renderResponse);

		String infoCot = CotizadorModularUtil.objtoJson(infCotizacion);

		renderRequest.setAttribute("infCotizacionJson", infoCot);
		renderRequest.setAttribute("inf", infCotizacion);
		renderRequest.setAttribute("perfilSuscriptor", perfilSuscriptor());
		renderRequest.setAttribute("retroactividad", diasRetroactividad());

		super.render(renderRequest, renderResponse);
	}

	private void llenaInfoCotizacion(RenderRequest renderRequest) {

		try {
			HttpServletRequest originalRequest = PortalUtil
					.getOriginalServletRequest(PortalUtil.getHttpServletRequest(renderRequest));

			user = (User) renderRequest.getAttribute(WebKeys.USER);
			idPerfilUser = (int) originalRequest.getSession().getAttribute("idPerfil");

			String inf = originalRequest.getParameter("infoCotizacion");
			String legal492 = originalRequest.getParameter("leg492");

			String nombreCotizador = "";
			if (Validator.isNotNull(inf)) {
				infCotizacion = CotizadorModularUtil.decodeURL(inf);
			} else if (Validator.isNotNull(legal492)) {
				infCotizacion = generaCotLegal(renderRequest);
			} else {

				infCotizacion = new InfoCotizacion();

				infCotizacion.setVersion(1);
				String uri = originalRequest.getRequestURI();
				if (uri.toLowerCase().contains("familiar")) {
					infCotizacion.setTipoCotizacion(TipoCotizacion.FAMILIAR);
				} else if (uri.toLowerCase().contains("empresarial")) {
					infCotizacion.setTipoCotizacion(TipoCotizacion.EMPRESARIAL);
				} else {
					infCotizacion.setTipoCotizacion(TipoCotizacion.EMPRESARIAL);
				}
			}

			switch (infCotizacion.getTipoCotizacion()) {
				case FAMILIAR:
					infCotizacion.setPantalla(CotizadorPaso1PortletKeys.PANTALLA_FAMILIAR);
					nombreCotizador = CotizadorPaso1PortletKeys.TITULO_FAMILIAR;
					break;
				case EMPRESARIAL:
					infCotizacion.setPantalla(CotizadorPaso1PortletKeys.PANTALLA_EMPRESARIAL);
					nombreCotizador = CotizadorPaso1PortletKeys.TITULO_EMPRESARIAL;
					break;
				default:
					infCotizacion.setPantalla("");
					nombreCotizador = "";
					break;
			}
			renderRequest.setAttribute("tituloCotizador", nombreCotizador);
		} catch (Exception e) {
			// TODO: handle exception
			System.err.println("------------------ llenaInfoCotizacion:");
			renderRequest.setAttribute("perfilMayorEjecutivo", false);
			e.printStackTrace();
		}
	}

	private void cargaCatalogos(RenderRequest renderRequest) {
		// TODO Auto-generated method stub
		try {

			final PortletSession psession = renderRequest.getPortletSession();
			@SuppressWarnings("unchecked")
			List<Persona> listaAgentes = (List<Persona>) psession.getAttribute("listaAgentes",
					PortletSession.APPLICATION_SCOPE);
			verificaListaAgentes(renderRequest, listaAgentes);

			// caso especial para endosos

			String pantallaEnd = esEndoso() ? "" : infCotizacion.getPantalla();
			

			ListaRegistro listaMovimiento = fGetCatalogos(
					CotizadorModularServiceKey.TMX_CTE_ROW_TODOS,
					CotizadorModularServiceKey.TMX_CTE_TRANSACCION_GET,
					CotizadorModularServiceKey.LIST_CAT_MOVIMIENTO,
					CotizadorModularServiceKey.TMX_CTE_CAT_ACTIVOS, user.getScreenName(),
					pantallaEnd, renderRequest);// (isEndoso ? "" :
												// p_pantalla))

			ListaRegistro listaCatMoneda = fGetCatalogos(
					CotizadorModularServiceKey.TMX_CTE_ROW_TODOS,
					CotizadorModularServiceKey.TMX_CTE_TRANSACCION_GET,
					CotizadorModularServiceKey.LIST_CAT_MONEDA,
					CotizadorModularServiceKey.TMX_CTE_CAT_ACTIVOS, user.getScreenName(),
					infCotizacion.getPantalla(), renderRequest);// (isEndoso ?
																// "" :
			// p_pantalla))

			ListaRegistro listaCatFormaPago = fGetCatalogos(
					CotizadorModularServiceKey.TMX_CTE_ROW_TODOS,
					CotizadorModularServiceKey.TMX_CTE_TRANSACCION_GET,
					CotizadorModularServiceKey.LIST_CAT_FORMA_PAGO,
					CotizadorModularServiceKey.TMX_CTE_CAT_ACTIVOS, user.getScreenName(),
					infCotizacion.getPantalla(), renderRequest);// (isEndoso ?
																// "" :
			// p_pantalla))

			ListaRegistro listaCatDenominacion = fGetCatalogos(
					CotizadorModularServiceKey.TMX_CTE_ROW_TODOS,
					CotizadorModularServiceKey.TMX_CTE_TRANSACCION_GET,
					CotizadorModularServiceKey.LIST_CAT_DENOMINACION,
					CotizadorModularServiceKey.TMX_CTE_CAT_ACTIVOS, user.getScreenName(),
					infCotizacion.getPantalla(), renderRequest);// (isEndoso ?
																// "" :
			// p_pantalla))

			if (infCotizacion.getTipoCotizacion() == TipoCotizacion.EMPRESARIAL) {
				ListaRegistro listaGiros = fGetCatalogos(
						CotizadorModularServiceKey.TMX_CTE_ROW_TODOS,
						CotizadorModularServiceKey.TMX_CTE_TRANSACCION_GET,
						CotizadorModularServiceKey.LIST_CAT_GIRO,
						CotizadorModularServiceKey.TMX_CTE_CAT_ACTIVOS, user.getScreenName(),
						infCotizacion.getPantalla(), renderRequest);// (isEndoso
																	// ? "" :
				// p_pantalla))
				
				if(infCotizacion.getModo() == ModoCotizacion.NUEVA) {
					listaGiros.getLista().removeIf(entry -> entry.getOtro().equals("0"));
				}

				renderRequest.setAttribute("listaGiros", listaGiros.getLista());
			}

			renderRequest.setAttribute("listaMovimiento", listaMovimiento.getLista());
			renderRequest.setAttribute("listaCatMoneda", listaCatMoneda.getLista());
			renderRequest.setAttribute("listaAgentes", listaAgentes);
			renderRequest.setAttribute("listaCatDenominacion", listaCatDenominacion.getLista());
			renderRequest.setAttribute("listaCatFormaPago", listaCatFormaPago.getLista());

		} catch (Exception e) {
			// TODO: handle exception
			System.err.println("------------------ cargaCatalogos:");
			e.printStackTrace();
		}

	}

	private ListaRegistro fGetCatalogos(int p_rownum, String p_tiptransaccion, String p_codigo,
			int p_activo, String p_usuario, String p_pantalla, RenderRequest renderRequest) {
		try {
			ListaRegistro lr = _CMServicesGenerico.getCatalogo(p_rownum, p_tiptransaccion, p_codigo,
					p_activo, p_usuario, p_pantalla);

			lr.getLista().sort(Comparator.comparing(Registro::getDescripcion));
			return lr;
		} catch (Exception e) {
			System.err.print("----------------- error en traer los catalogos");
			e.printStackTrace();
			SessionErrors.add(renderRequest, "errorConocido");
			renderRequest.setAttribute("errorMsg", "Error en catalogos");
			SessionMessages.add(renderRequest, PortalUtil.getPortletId(renderRequest)
					+ SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_ERROR_MESSAGE);
			return null;
		}
	}

	private void generaFechas(RenderRequest renderRequest) {
		LocalDate fechaHoy = LocalDate.now();
		LocalDate fechaMasAnio = LocalDate.now().plusYears(1);

		renderRequest.setAttribute("fechaHoy", fechaHoy);
		renderRequest.setAttribute("fechaMasAnio", fechaMasAnio);
		renderRequest.setAttribute("perfilMayorEjecutivo", perfilPermisosGeneral());
	}

	private boolean perfilPermisosGeneral() {
		try {
			switch (idPerfilUser) {
				case CotizadorPaso1PortletKeys.PERFIL_EJECUTIVO:
					return true;
				case CotizadorPaso1PortletKeys.PERFIL_SUSCRIPTORJR:
					return true;
				case CotizadorPaso1PortletKeys.PERFIL_SUSCRIPTORSR:
					return true;
				case CotizadorPaso1PortletKeys.PERFIL_SUSCRIPTORMR:
					return true;
			}
			return false;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
	}

	private void seleccionaModo(RenderRequest renderRequest, RenderResponse renderResponse) {
		CotizadorDataResponse respuesta = new CotizadorDataResponse();
		respuesta.setCode(5);
		respuesta.setMsg("Error al cargar su información");
		try {
			switch (infCotizacion.getModo()) {
				case EDICION:
					respuesta = _CMServicesP1.getCotizadorData(infCotizacion.getFolio(),
							infCotizacion.getCotizacion(), infCotizacion.getVersion(),
							user.getScreenName(), infCotizacion.getPantalla());
					validaFolioUsuario((int)infCotizacion.getCotizacion(), infCotizacion.getVersion(), idPerfilUser, user.getScreenName(), infCotizacion.getPantalla(), renderResponse);
					break;
				case COPIA:
					respuesta = _CMServicesP1.copyCotizadorData(infCotizacion.getFolio() + "",
							Integer.parseInt(infCotizacion.getCotizacion() + ""),
							infCotizacion.getVersion(), user.getScreenName(),
							infCotizacion.getPantalla());

					infCotizacion
							.setFolio(Long.parseLong(respuesta.getDatosCotizacion().getFolio()));
					infCotizacion.setCotizacion(respuesta.getDatosCotizacion().getCotizacion());
					infCotizacion.setVersion(respuesta.getDatosCotizacion().getVersion());
					validaFolioUsuario((int)infCotizacion.getCotizacion(), infCotizacion.getVersion(), idPerfilUser, user.getScreenName(), infCotizacion.getPantalla(), renderResponse);
					break;
				case ALTA_ENDOSO:
					SimpleResponse infEndo = _CMServicesP1.GuardarCotizacionEndoso(
							infCotizacion.getCotizacion() + "", infCotizacion.getVersion() + "",
							infCotizacion.getPantalla(), user.getScreenName());

					infCotizacion.setFolio(Long.parseLong(infEndo.getFolio()));
					infCotizacion.setCotizacion(infEndo.getCotizacion());
					infCotizacion.setVersion(infEndo.getVersion());

					respuesta = _CMServicesP1.getCotizadorData(infCotizacion.getFolio(),
							infCotizacion.getCotizacion(), infCotizacion.getVersion(),
							user.getScreenName(), infCotizacion.getPantalla());
					
					validaFolioUsuario((int)infCotizacion.getCotizacion(), infCotizacion.getVersion(), idPerfilUser, user.getScreenName(), infCotizacion.getPantalla(), renderResponse);
					
					renderRequest.setAttribute("perfilMayorEjecutivo", false);
					break;
				case EDITAR_ALTA_ENDOSO:
					respuesta = _CMServicesP1.getCotizadorData(infCotizacion.getFolio(),
							infCotizacion.getCotizacion(), infCotizacion.getVersion(),
							user.getScreenName(), infCotizacion.getPantalla());
					
					validaFolioUsuario((int)infCotizacion.getCotizacion(), infCotizacion.getVersion(), idPerfilUser, user.getScreenName(), infCotizacion.getPantalla(), renderResponse);
					
					renderRequest.setAttribute("perfilMayorEjecutivo", false);
					break;
				case BAJA_ENDOSO:
					
					SimpleResponse simpleRespuesta = _CMServicesGenerico.guardarCotizacionEndosoBaja(infCotizacion.getCotizacion(),
							infCotizacion.getVersion(), null, 1, 0, 0,
							user.getScreenName() , infCotizacion.getPantalla(), 0, 0);
					
					infCotizacion.setFolio(Long.parseLong(simpleRespuesta.getFolio()));
					infCotizacion.setCotizacion(simpleRespuesta.getCotizacion());
					infCotizacion.setVersion(simpleRespuesta.getVersion());					

					respuesta = _CMServicesP1.getCotizadorData(Long.parseLong(simpleRespuesta.getFolio()),
							simpleRespuesta.getCotizacion(), simpleRespuesta.getVersion(),
							user.getScreenName(), infCotizacion.getPantalla());
					
					validaFolioUsuario((int)infCotizacion.getCotizacion(), infCotizacion.getVersion(), idPerfilUser, user.getScreenName(), infCotizacion.getPantalla(), renderResponse);
					
					renderRequest.setAttribute("perfilMayorEjecutivo", false);
					break;
				case EDITAR_BAJA_ENDOSO:
					respuesta = _CMServicesP1.getCotizadorData(infCotizacion.getFolio(),
							infCotizacion.getCotizacion(), infCotizacion.getVersion(),
							user.getScreenName(), infCotizacion.getPantalla());
					
					infCotizacion.setModo(ModoCotizacion.BAJA_ENDOSO);
					
					validaFolioUsuario((int)infCotizacion.getCotizacion(), infCotizacion.getVersion(), idPerfilUser, user.getScreenName(), infCotizacion.getPantalla(), renderResponse);
					
					renderRequest.setAttribute("perfilMayorEjecutivo", false);
					break;
				case AUX_PASO4:

					break;
				case NUEVA:
					/*
					if(infCotizacion.getTipoCotizacion().equals(TipoCotizacion.FAMILIAR)) {
						renderRequest.setAttribute("bloqueaNuevaFamiliar", true);
					}
					*/
					break;
				case CONSULTA:
					respuesta = _CMServicesP1.getCotizadorData(infCotizacion.getFolio(),
							infCotizacion.getCotizacion(), infCotizacion.getVersion(),
							user.getScreenName(), infCotizacion.getPantalla());
					validaFolioUsuario((int)infCotizacion.getCotizacion(), infCotizacion.getVersion(), idPerfilUser, user.getScreenName(), infCotizacion.getPantalla(), renderResponse);
					
					break;
					
				case FACTURA_492 :
						respuesta = _CMServicesP1.getCotizadorData(infCotizacion.getFolio(),
								infCotizacion.getCotizacion(), infCotizacion.getVersion(),
								user.getScreenName(), infCotizacion.getPantalla());
						validaFolioUsuario((int)infCotizacion.getCotizacion(), infCotizacion.getVersion(), idPerfilUser, user.getScreenName(), infCotizacion.getPantalla(), renderResponse);
						break;
				
				case CONSULTAR_REVISION:
					respuesta = _CMServicesP1.getCotizadorData(infCotizacion.getFolio(),
							infCotizacion.getCotizacion(), infCotizacion.getVersion(),
							user.getScreenName(), infCotizacion.getPantalla());
					validaFolioUsuario((int)infCotizacion.getCotizacion(), infCotizacion.getVersion(), idPerfilUser, user.getScreenName(), infCotizacion.getPantalla(), renderResponse);
					break;
				
				case EDITAR_RENOVACION_AUTOMATICA: 
						respuesta = _CMServicesP1.getCotizadorData(infCotizacion.getFolio(),
								infCotizacion.getCotizacion(), infCotizacion.getVersion(),
								user.getScreenName(), infCotizacion.getPantalla());
						validaFolioUsuario((int)infCotizacion.getCotizacion(), infCotizacion.getVersion(), idPerfilUser, user.getScreenName(), infCotizacion.getPantalla(), renderResponse);
						break;
				default:
					break;

			}

			if (respuesta.getDatosCotizacion().getDatosCliente().getTipoPer() == 218) {
				infCotizacion.setTipoPersona(TipoPersona.MORAL);
			} else {
				infCotizacion.setTipoPersona(TipoPersona.FISICA);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		if (infCotizacion.getModo() != ModoCotizacion.NUEVA) {

			if (respuesta.getCode() > 0) {
				SessionErrors.add(renderRequest, "errorConocido");
				renderRequest.setAttribute("errorMsg", respuesta.getMsg());
				SessionMessages.add(renderRequest, PortalUtil.getPortletId(renderRequest)
						+ SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_ERROR_MESSAGE);
			} else {
				String datosCliente = CotizadorModularUtil
						.objtoJson(respuesta.getDatosCotizacion().getDatosCliente());

				LocalDate fechaHoy = generaFecha(respuesta.getDatosCotizacion().getFecInicio());
				LocalDate fechaMasAnio = generaFecha(respuesta.getDatosCotizacion().getFecFin());

				if (infCotizacion.getTipoCotizacion().equals(TipoCotizacion.EMPRESARIAL)) {
					getSubgiro(renderRequest, respuesta.getDatosCotizacion().getGiro());
				}

				fechaHoy = validaCambioFecha(fechaHoy);

				renderRequest.setAttribute("fechaHoy", fechaHoy);
				renderRequest.setAttribute("fechaMasAnio", fechaMasAnio);
				renderRequest.setAttribute("cotizadorData", respuesta.getDatosCotizacion());
				renderRequest.setAttribute("datosCliente", datosCliente);
				//renderRequest.setAttribute("bloqueaNuevaFamiliar", false);

			}
		}
	}

	private LocalDate generaFecha(String fecha) {
		String aux = "";
		for (char c : fecha.toCharArray()) {
			aux += Character.isDigit(c) ? c : "";
		}
		Timestamp t = new Timestamp(Long.parseLong(aux));
		return t.toLocalDateTime().toLocalDate();
	}

	private void getSubgiro(RenderRequest renderRequest, int giro) {
		try {
			ListaRegistro catalogo = _CMServicesP1.wsCatalogosDetallePadre(
					CotizadorModularServiceKey.TMX_CTE_ROW_TODOS,
					CotizadorModularServiceKey.TMX_CTE_TRANSACCION_GET, giro,
					CotizadorModularServiceKey.TMX_CTE_CAT_ACTIVOS, user.getScreenName(),
					infCotizacion.getPantalla());

			catalogo.getLista().sort(Comparator.comparing(Registro::getDescripcion));

			renderRequest.setAttribute("listaSubGiro", catalogo.getLista());
		} catch (CotizadorModularException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private LocalDate fechaMayor(LocalDate fechaOriginal) {
		LocalDate hoy = LocalDate.now();
		if (hoy.isAfter(fechaOriginal)) {
			return hoy;
		}
		return fechaOriginal;
	}

	private LocalDate validaCambioFecha(LocalDate fechaOriginal) {
		switch (infCotizacion.getModo()) {
			case ALTA_ENDOSO:
				return fechaMayor(fechaOriginal);
			case BAJA_ENDOSO:
				return fechaMayor(fechaOriginal);
			case EDITAR_ALTA_ENDOSO:
				return fechaMayor(fechaOriginal);
			case EDITAR_BAJA_ENDOSO:
				return fechaMayor(fechaOriginal);
			default:
				return fechaOriginal;
		}
	}

	private int perfilSuscriptor() {
		try {
			switch (idPerfilUser) {
				case CotizadorPaso1PortletKeys.PERFIL_SUSCRIPTORJR:
					return 1;
				case CotizadorPaso1PortletKeys.PERFIL_SUSCRIPTORSR:
					return 1;
				case CotizadorPaso1PortletKeys.PERFIL_SUSCRIPTORMR:
					return 1;
			}
			return 0;
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}
	}

	private void verificaListaAgentes(RenderRequest renderRequest, List<Persona> listaAgentes) {
		if (Validator.isNull(listaAgentes)) {
			SessionErrors.add(renderRequest, "errorConocido");
			renderRequest.setAttribute("errorMsg", "Error al cargar su información cierre sesion");
			SessionMessages.add(renderRequest, PortalUtil.getPortletId(renderRequest)
					+ SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_ERROR_MESSAGE);
		}
	}
	
	private InfoCotizacion generaCotLegal(RenderRequest renderRequest){
		HttpServletRequest originalRequest = PortalUtil
				.getOriginalServletRequest(PortalUtil.getHttpServletRequest(renderRequest));
		
		InfoCotizacion in = new InfoCotizacion();
		
		String uri = originalRequest.getRequestURI();
		if (uri.toLowerCase().contains("familiar")) {
			in.setTipoCotizacion(TipoCotizacion.FAMILIAR);
			in.setFolio(Long.parseLong(originalRequest.getParameter("folioFamiliar")));
			in.setCotizacion(Long.parseLong(originalRequest.getParameter("cotizacionFamiliar")));
			in.setVersion(Integer.parseInt(originalRequest.getParameter("versionFamiliar")));
		} else if (uri.toLowerCase().contains("empresarial")) {
			in.setTipoCotizacion(TipoCotizacion.EMPRESARIAL);
			in.setFolio(Long.parseLong(originalRequest.getParameter("folioEmpresarial")));
			in.setCotizacion(Long.parseLong(originalRequest.getParameter("cotizacionEmpresarial")));
			in.setVersion(Integer.parseInt(originalRequest.getParameter("versionEmpresarial")));
		} 
		
		in.setModo(ModoCotizacion.FACTURA_492);
		
		System.out.println("-----------");
		System.out.println(in.toString());
		return in;
		
	}
	
	
	boolean esEndoso(){
		switch (infCotizacion.getModo()) {
			case ALTA_ENDOSO:				
				return true;
			case BAJA_ENDOSO:				
				return true;
			case EDITAR_ALTA_ENDOSO:				
				return true;
			case EDITAR_BAJA_ENDOSO:				
				return true;
			default:
				return false;
				
		}
		
	}

	void generaAuxBajaEndoso(long fol, long cot, int ver, RenderRequest renderRequest){
		
		final PortletSession psession = renderRequest.getPortletSession();
		
		
		
		String nombreDatosGenerales = "LIFERAY_SHARED_F=" + infCotizacion.getFolio() +
				"_C=" + infCotizacion.getCotizacion() +
				"_V=" + infCotizacion.getVersion() +
				"_AUXBAJAEND";
		
		SimpleResponse sr = new SimpleResponse();
		sr.setCode(0);
		sr.setCotizacion((int) fol);
		sr.setFolio(cot + "");
		sr.setVersion(ver);
		String auxEnd = CotizadorModularUtil.objtoJson(sr);
		renderRequest.setAttribute("AUXBAJAEND", auxEnd);
		psession.setAttribute(nombreDatosGenerales, auxEnd, PortletSession.APPLICATION_SCOPE);
	}
	
	private void validaFolioUsuario(int cotizacion, int version, int perfilId, String usuario, String pantalla, RenderResponse renderResponse) throws IOException{
		SimpleResponse resp = new SimpleResponse();
		try {
			resp = _CMServicesGenerico.validaFolioUsuario(cotizacion, version, perfilId, usuario, pantalla);
		} catch (Exception e) {
			// TODO: handle exception	
			System.err.println("Error al validar permisos por perfil");
			resp.setCode(1);
		}finally {
			if( resp.getCode() != 0 ){
				PortalUtil.getHttpServletResponse(renderResponse).sendRedirect("/group/portal-agentes/" );
			}						
		}
	}
	
	private int diasRetroactividad() {
		switch (idPerfilUser) {
			case CotizadorPaso1PortletKeys.PERFIL_SUSCRIPTORJR:
				return CotizadorPaso1PortletKeys.DIAS_RETROACTIVOS_SUSCRIPTORJR;
			case CotizadorPaso1PortletKeys.PERFIL_SUSCRIPTORSR:
				return CotizadorPaso1PortletKeys.DIAS_RETROACTIVOS_SUSCRIPTORSR;
			case CotizadorPaso1PortletKeys.PERFIL_SUSCRIPTORMR:
				return CotizadorPaso1PortletKeys.DIAS_RETROACTIVOS_SUSCRIPTORMR;
			case CotizadorPaso1PortletKeys.PERFIL_JAPONES:
				return CotizadorPaso1PortletKeys.DIAS_RETROACTIVOS_SUSCRIPTORJR;
			default: return 0;
		}
	}
	
	
	
	
	
//	String a = "{\"code\":0,\"msg\":\"PROCESO TERMINADO CON EXITO\",\"primaNeta\":0,\"recargo\":0,\"gastos\":1100,\"iva\":176,\"total\":1276,\"estado\":310,\"email\":\"agentepruebas@tokiomarine.com.mx\",\"datosCaratula\":[{\"ubicaciones\":[{\"titulo\":\"Incendio-edificio\",\"contenedor\":\"Incendio Edificio\",\"ubicacion\":2,\"sa\":\"        $40,000,000.00\",\"prima\":\"           -$15,707.91\",\"deducible\":\"Sin deducible\"},{\"titulo\":\"Incendio-edificio\",\"contenedor\":\"Incendio Edificio\",\"ubicacion\":3,\"sa\":\"        $40,000,000.00\",\"prima\":\"           -$15,707.91\",\"deducible\":\"Sin deducible\"}],\"titulo\":\"Incendio-edificio\",\"contenedor\":\"Incendio Edificio\"},{\"ubicaciones\":[{\"titulo\":\"Incendio-contenidos\",\"contenedor\":\"Incendio Contenidos\",\"ubicacion\":2,\"sa\":\"        $20,000,000.00\",\"prima\":\"            -$7,853.96\",\"deducible\":\"Sin deducible\"},{\"titulo\":\"Incendio-contenidos\",\"contenedor\":\"Incendio Contenidos\",\"ubicacion\":3,\"sa\":\"        $20,000,000.00\",\"prima\":\"            -$7,853.96\",\"deducible\":\"Sin deducible\"}],\"titulo\":\"Incendio-contenidos\",\"contenedor\":\"Incendio Contenidos\"},{\"ubicaciones\":[{\"titulo\":\"Bienes bajo convenio expreso\",\"contenedor\":\"Incendio Contenidos\",\"ubicacion\":3,\"sa\":\"         $3,000,000.00\",\"prima\":\"Amparada\",\"deducible\":\"5% sobre la suma asegurada de éstos bienes\"},{\"titulo\":\"Bienes bajo convenio expreso\",\"contenedor\":\"Incendio Contenidos\",\"ubicacion\":2,\"sa\":\"         $3,000,000.00\",\"prima\":\"Amparada\",\"deducible\":\"5% sobre la suma asegurada de éstos bienes\"}],\"titulo\":\"Bienes bajo convenio expreso\",\"contenedor\":\"Incendio Contenidos\"},{\"ubicaciones\":[{\"titulo\":\"Gastos extraordinarios\",\"contenedor\":\"Pérdidas Consecuenciales\",\"ubicacion\":3,\"sa\":\"Excluido\",\"prima\":\"Excluido\",\"deducible\":\"Excluido\"},{\"titulo\":\"Gastos extraordinarios\",\"contenedor\":\"Pérdidas Consecuenciales\",\"ubicacion\":2,\"sa\":\"Excluido\",\"prima\":\"Excluido\",\"deducible\":\"Excluido\"}],\"titulo\":\"Gastos extraordinarios\",\"contenedor\":\"Pérdidas Consecuenciales\"},{\"ubicaciones\":[{\"titulo\":\"Pérdida de rentas\",\"contenedor\":\"Pérdidas Consecuenciales\",\"ubicacion\":3,\"sa\":\"Excluido\",\"prima\":\"Excluido\",\"deducible\":\"Excluido\"},{\"titulo\":\"Pérdida de rentas\",\"contenedor\":\"Pérdidas Consecuenciales\",\"ubicacion\":2,\"sa\":\"Excluido\",\"prima\":\"Excluido\",\"deducible\":\"Excluido\"}],\"titulo\":\"Pérdida de rentas\",\"contenedor\":\"Pérdidas Consecuenciales\"},{\"ubicaciones\":[{\"titulo\":\"Rotura de cristales\",\"contenedor\":\"Rotura de Cristales\",\"ubicacion\":3,\"sa\":\"Excluido\",\"prima\":\"Excluido\",\"deducible\":\"Excluido\"},{\"titulo\":\"Rotura de cristales\",\"contenedor\":\"Rotura de Cristales\",\"ubicacion\":2,\"sa\":\"           $500,000.00\",\"prima\":\"            -$5,890.47\",\"deducible\":\"5% sobre la pérdida con mínimo de 3 DSMGVDF\"}],\"titulo\":\"Rotura de cristales\",\"contenedor\":\"Rotura de Cristales\"},{\"ubicaciones\":[{\"titulo\":\"Robo Seccion I\",\"contenedor\":\"Robo de Contenidos\",\"ubicacion\":3,\"sa\":\"Excluido\",\"prima\":\"Excluido\",\"deducible\":\"Excluido\"},{\"titulo\":\"Robo Seccion I\",\"contenedor\":\"Robo de Contenidos\",\"ubicacion\":2,\"sa\":\"Excluido\",\"prima\":\"Excluido\",\"deducible\":\"Excluido\"}],\"titulo\":\"Robo Seccion I\",\"contenedor\":\"Robo de Contenidos\"},{\"ubicaciones\":[{\"titulo\":\"Robo Seccion II\",\"contenedor\":\"Robo de Contenidos\",\"ubicacion\":2,\"sa\":\"Excluido\",\"prima\":\"Excluido\",\"deducible\":\"Excluido\"},{\"titulo\":\"Robo Seccion II\",\"contenedor\":\"Robo de Contenidos\",\"ubicacion\":3,\"sa\":\"           $800,000.00\",\"prima\":\"            -$3,926.98\",\"deducible\":\"10% De la reclamación con mínimo de 50 DSMGVDF\"}],\"titulo\":\"Robo Seccion II\",\"contenedor\":\"Robo de Contenidos\"},{\"ubicaciones\":[{\"titulo\":\"Robo Seccion III\",\"contenedor\":\"Robo de Contenidos\",\"ubicacion\":2,\"sa\":\"Excluido\",\"prima\":\"Excluido\",\"deducible\":\"Excluido\"},{\"titulo\":\"Robo Seccion III\",\"contenedor\":\"Robo de Contenidos\",\"ubicacion\":3,\"sa\":\"Excluido\",\"prima\":\"Excluido\",\"deducible\":\"Excluido\"}],\"titulo\":\"Robo Seccion III\",\"contenedor\":\"Robo de Contenidos\"},{\"ubicaciones\":[{\"titulo\":\"Dinero y valores (dentro y fuera)\",\"contenedor\":\"Diversos Miscelaneos\",\"ubicacion\":2,\"sa\":\"Excluido\",\"prima\":\"Excluido\",\"deducible\":\"Excluido\"},{\"titulo\":\"Dinero y valores (dentro y fuera)\",\"contenedor\":\"Diversos Miscelaneos\",\"ubicacion\":3,\"sa\":\"Excluido\",\"prima\":\"Excluido\",\"deducible\":\"Excluido\"}],\"titulo\":\"Dinero y valores (dentro y fuera)\",\"contenedor\":\"Diversos Miscelaneos\"},{\"ubicaciones\":[{\"titulo\":\"Responsabilidad civil\",\"contenedor\":\"Responsabilidad Civil\",\"ubicacion\":2,\"sa\":\"        $19,000,000.00\",\"prima\":\"            -$7,677.24\",\"deducible\":\"Sin deducible\"},{\"titulo\":\"Responsabilidad civil\",\"contenedor\":\"Responsabilidad Civil\",\"ubicacion\":3,\"sa\":\"        $19,000,000.00\",\"prima\":\"            -$7,677.24\",\"deducible\":\"Sin deducible\"}],\"titulo\":\"Responsabilidad civil\",\"contenedor\":\"Responsabilidad Civil\"},{\"ubicaciones\":[{\"titulo\":\"Equipo electronico clasico\",\"contenedor\":\"Diversos Técnicos\",\"ubicacion\":3,\"sa\":\"Excluido\",\"prima\":\"Excluido\",\"deducible\":\"Excluido\"},{\"titulo\":\"Equipo electronico clasico\",\"contenedor\":\"Diversos Técnicos\",\"ubicacion\":2,\"sa\":\"Excluido\",\"prima\":\"Excluido\",\"deducible\":\"Excluido\"}],\"titulo\":\"Equipo electronico clasico\",\"contenedor\":\"Diversos Técnicos\"},{\"ubicaciones\":[{\"titulo\":\"Equipo electrónico móvil y portátil\",\"contenedor\":\"Diversos Técnicos\",\"ubicacion\":2,\"sa\":\"Excluido\",\"prima\":\"Excluido\",\"deducible\":\"Excluido\"},{\"titulo\":\"Equipo electrónico móvil y portátil\",\"contenedor\":\"Diversos Técnicos\",\"ubicacion\":3,\"sa\":\"Excluido\",\"prima\":\"Excluido\",\"deducible\":\"Excluido\"}],\"titulo\":\"Equipo electrónico móvil y portátil\",\"contenedor\":\"Diversos Técnicos\"},{\"ubicaciones\":[{\"titulo\":\"Servicio de Asistencia en el Hogar\",\"contenedor\":\"Beneficios Adicionales\",\"ubicacion\":3,\"sa\":\"Amparada\",\"prima\":\"Amparada\",\"deducible\":\"Sin deducible\"},{\"titulo\":\"Servicio de Asistencia en el Hogar\",\"contenedor\":\"Beneficios Adicionales\",\"ubicacion\":2,\"sa\":\"Amparada\",\"prima\":\"Amparada\",\"deducible\":\"Sin deducible\"}],\"titulo\":\"Servicio de Asistencia en el Hogar\",\"contenedor\":\"Beneficios Adicionales\"}]}";

}