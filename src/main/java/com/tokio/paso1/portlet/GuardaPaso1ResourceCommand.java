/**
 * 
 */
package com.tokio.paso1.portlet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.tokio.cotizadorModular.Bean.InfoAuxPaso1;
import com.tokio.cotizadorModular.Bean.InfoCotizacion;
import com.tokio.cotizadorModular.Bean.InfoCotizacionOrigen;
import com.tokio.cotizadorModular.Bean.UbicacionesResponse;
import com.tokio.cotizadorModular.Enum.ModoCotizacion;
import com.tokio.cotizadorModular.Enum.TipoCotizacion;
import com.tokio.cotizadorModular.Exception.CotizadorModularException;
import com.tokio.cotizadorModular.Interface.CotizadorGenerico;
import com.tokio.cotizadorModular.Interface.CotizadorPaso1;
import com.tokio.cotizadorModular.Util.CotizadorModularUtil;
import com.tokio.paso1.bean.DatosGenerales;
import com.tokio.paso1.constants.CotizadorPaso1PortletKeys;

import java.io.PrintWriter;

import javax.portlet.PortletSession;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author jonathanfviverosmoreno
 *
 */

@Component(
		immediate = true,
		property = {
				"javax.portlet.name=" + CotizadorPaso1PortletKeys.PORTLET_NAME,
					"mvc.command.name=/cotizadores/paso1/guardaPaso" 
				},
		service = MVCResourceCommand.class)

public class GuardaPaso1ResourceCommand extends BaseMVCResourceCommand {

	/* (non-Javadoc)
	 * @see com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand#doServeResource(javax.portlet.ResourceRequest, javax.portlet.ResourceResponse)
	 */
	
	@Reference
	CotizadorPaso1 _CMServicesP1;
	
	@Reference
	CotizadorGenerico _CMServicesGen;
	
	@Override
	protected void doServeResource(ResourceRequest resourceRequest,
			ResourceResponse resourceResponse) throws Exception {
		// TODO Auto-generated method stub
		Gson gson = new Gson();

		/************************** Validación metodo post **************************/
		if ( !resourceRequest.getMethod().equals("POST")  ){
			JsonObject requestError = new JsonObject();
			requestError.addProperty("code", 500);
			requestError.addProperty("msg", "Error en tipo de consulta");
			PrintWriter writer = resourceResponse.getWriter();
			writer.write(requestError.toString());
			return;
		}
		/************************** Validación metodo post **************************/
		
		HttpServletRequest originalRequest = PortalUtil
				.getOriginalServletRequest(PortalUtil.getHttpServletRequest(resourceRequest));

		
		
		String datos = ParamUtil.getString(resourceRequest, "datos");
		String infoCot = ParamUtil.getString(resourceRequest, "infoCot");
		
		System.out.println(datos);
		
		DatosGenerales dg = gson.fromJson(datos, DatosGenerales.class);
		
		InfoCotizacion infCot = gson.fromJson(infoCot, InfoCotizacion.class);
		
		
		
		User user = (User) resourceRequest.getAttribute(WebKeys.USER);
		String usuario = user.getScreenName();
		int idPerfilUser = (int) originalRequest.getSession().getAttribute("idPerfil");
		
		
		int code = 0;
		String msj = "";
		String url = "";
		
		UbicacionesResponse ubicacionResponse = null;
		switch (dg.getTipoCot()) {
			case EMPRESARIAL:
				ubicacionResponse  = guardaCotizacionEmpresarial(dg, usuario, idPerfilUser);
				break;
			case FAMILIAR:
				ubicacionResponse = guardaCotizacionFamiliar(dg, usuario, idPerfilUser);
				break;
			default:
				code = 5;
				msj = "Error al guardar la información";
				System.err.println("---------------------_> error al guardar <----------------------------");
				break;

		}
		
		if(esBajaEndoso(infCot.getModo())){
			ubicacionResponse = auxUbicacionesBajas(ubicacionResponse, 
					usuario, dg.getPantalla(), infCot.getTipoCotizacion(), infCot.getModo());
		}
		
		System.out.println(ubicacionResponse.toString());
		
		if(code == 0){
			infCot.setCotizacion(ubicacionResponse.getCotizacion());
			infCot.setFolio(Long.parseLong( ubicacionResponse.getFolio()));
			infCot.setVersion(ubicacionResponse.getVersion());
			
			
			
			url =  generaUrl(infCot, resourceRequest);
			msj = "paso 2";
			
			generaVarSesion(ubicacionResponse, dg, resourceRequest);
			
		}
		
		
		PrintWriter writer = resourceResponse.getWriter();
		writer.write("{\"code\" : " + code +
				" , \"msg\" : \"" + msj +
				" \", \"url\" : \"" + url +
				"\" }");

	}
	
	private UbicacionesResponse guardaCotizacionEmpresarial(DatosGenerales dg, String usuario, int idPerfil) {
		try {
			int modo = (dg.getModo().getModoCotizacion() >= 3 ) ? 1 : dg.getModo().getModoCotizacion();
			UbicacionesResponse cer = 
					_CMServicesP1.guardarCotizacionEmpresarial(dg.getTipomov(), dg.getVigencia(), dg.getFecinicio(),
							dg.getFecfin(), dg.getMoneda(), dg.getFormapago(), dg.getAgente(), dg.getIdPersona(), dg.getTipoPer(),
							dg.getRfc(), dg.getNombre(), dg.getAppPaterno(), dg.getAppMaterno(), dg.getIdDenominacion(), dg.getCodigo(),
							dg.getExtranjero(), modo + "", dg.getCotizacion(), dg.getVersion(), dg.getGiro(), dg.getSubGiro(), 
							dg.getFolio(), dg.getDetalleSubGiro(), usuario, dg.getPantalla(), idPerfil, dg.getP_permisoSubgiro());
			
			return cer;
		} catch (CotizadorModularException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	private UbicacionesResponse guardaCotizacionFamiliar(DatosGenerales dg, String usuario, int idPerfil) {		
		try {
			int modo = (dg.getModo().getModoCotizacion() >= 3 ) ? 1 : dg.getModo().getModoCotizacion();
			UbicacionesResponse cfr =
					_CMServicesP1.guardarCotizacionFamiliar(dg.getTipomov(), dg.getVigencia(), dg.getFecinicio(),
							dg.getFecfin(), dg.getMoneda(), dg.getFormapago(), dg.getAgente(), dg.getIdPersona(), dg.getTipoPer(),
							dg.getRfc(), dg.getNombre(), dg.getAppPaterno(), dg.getAppMaterno(), dg.getIdDenominacion(), dg.getCodigo(),
							dg.getExtranjero(), modo, Integer.parseInt(dg.getCotizacion()), dg.getVersion(),
							dg.getFolio(), usuario, dg.getPantalla(),
							idPerfil,  dg.getP_permisoSubgiro());
			
			return cfr;
		} catch (NumberFormatException | CotizadorModularException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	private String generaUrl(InfoCotizacion infCot, ResourceRequest resourceRequest){
		try {
			HttpServletRequest originalRequest = PortalUtil
					.getOriginalServletRequest(PortalUtil.getHttpServletRequest(resourceRequest));
			ThemeDisplay themeDisplay = (ThemeDisplay) resourceRequest
					.getAttribute(WebKeys.THEME_DISPLAY);
			String parametro = "?infoCotizacion=" + CotizadorModularUtil.encodeURL(infCot);
			final long GROUP_ID = themeDisplay.getLayout().getGroupId();
			Layout layout = LayoutLocalServiceUtil.getFriendlyURLLayout(GROUP_ID, true, "/paso2");
			String urlCotizador = layout.getRegularURL(originalRequest);
			
			
			return (urlCotizador + parametro);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return "";
		}
		
		
	}
	
	private void generaVarSesion(UbicacionesResponse ubicacionResponse, DatosGenerales dg, ResourceRequest resourceRequest){
		final PortletSession psession = resourceRequest.getPortletSession();
		
		String ubicacionString =CotizadorModularUtil.objtoJson(ubicacionResponse);
		String nombreUbicaciones = "LIFERAY_SHARED_F=" + ubicacionResponse.getFolio() +
				"_C=" + ubicacionResponse.getCotizacion() +
				"_V=" + ubicacionResponse.getVersion() +
				"_UBICACIONRESPONSE";
		
		String nombreDatosGenerales = "LIFERAY_SHARED_F=" + ubicacionResponse.getFolio() +
				"_C=" + ubicacionResponse.getCotizacion() +
				"_V=" + ubicacionResponse.getVersion() +
				"_DATOSP1";

		String nombreSubGiroRiesgo = "LIFERAY_SHARED_F=" + ubicacionResponse.getFolio() +
				"_C=" + ubicacionResponse.getCotizacion() +
				"_V=" + ubicacionResponse.getVersion() +
				"_SUBGIRORIESGO";
		
		InfoAuxPaso1 p1 = new InfoAuxPaso1();
		p1.setMonedaSeleccionada(dg.getMoneda() +"");
		p1.setSubgiroRiesgo(dg.getP_permisoSubgiro() > 0);
		p1.setSubEstado(dg.getSubEstado());
		String paso1 = CotizadorModularUtil.objtoJson(p1);
		
		psession.setAttribute(nombreUbicaciones, ubicacionString, PortletSession.APPLICATION_SCOPE);
		psession.setAttribute(nombreDatosGenerales, paso1, PortletSession.APPLICATION_SCOPE);
		psession.setAttribute(nombreSubGiroRiesgo, dg.getP_permisoSubgiro(), PortletSession.APPLICATION_SCOPE);
		
		System.out.println("del paso 1 :" + paso1);
	}
	
	private boolean esBajaEndoso(ModoCotizacion mc){
		switch (mc) {
			case BAJA_ENDOSO:
				
				return true;
			case EDITAR_BAJA_ENDOSO:
				
				return true;

			default:
				return false;
		}
	}
	
	private UbicacionesResponse auxUbicacionesBajas(UbicacionesResponse ubBajas,
			String usuario, String pantalla, TipoCotizacion tipoCotizacion, ModoCotizacion modoCotizacion){
		try {
			InfoCotizacionOrigen cotOrigen = _CMServicesGen.GetOrigenEndoso(ubBajas.getCotizacion(), 
					ubBajas.getVersion(), usuario, pantalla);
			
			String webservice = tipoCotizacion.equals(TipoCotizacion.EMPRESARIAL) ? 
					CotizadorPaso1PortletKeys.CONSULTA_UB_EMPRESARIAL :
						CotizadorPaso1PortletKeys.CONSULTA_UB_FAMILIAR;
			String direction = CotizadorPaso1PortletKeys.SERV_DIRECCION + webservice;
			
			UbicacionesResponse ub = _CMServicesP1.consultaUbicaciones((int)cotOrigen.getP_cotizacion_origen(),
					cotOrigen.getP_version_origen(), pantalla, usuario, direction, webservice);
			
			ubBajas.setDataProviders(ub.getDataProviders());
			ubBajas.setUbicaciones(ub.getUbicaciones());
		} catch (CotizadorModularException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ubBajas;
	}

	
}
