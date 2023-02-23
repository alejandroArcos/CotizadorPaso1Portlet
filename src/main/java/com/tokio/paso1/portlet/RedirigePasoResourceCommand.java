/**
 * 
 */
package com.tokio.paso1.portlet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.tokio.cotizadorModular.Bean.InfoCotizacion;
import com.tokio.cotizadorModular.Util.CotizadorModularUtil;
import com.tokio.paso1.constants.CotizadorPaso1PortletKeys;

import java.io.PrintWriter;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;

/**
 * @author jonathanfviverosmoreno
 *
 */

@Component(
		immediate = true,
		property = {
				"javax.portlet.name=" + CotizadorPaso1PortletKeys.PORTLET_NAME,
					"mvc.command.name=/cotizadores/paso1/redirigePasoX" 
				},
		service = MVCResourceCommand.class)

public class RedirigePasoResourceCommand extends BaseMVCResourceCommand {

	/* (non-Javadoc)
	 * @see com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand#doServeResource(javax.portlet.ResourceRequest, javax.portlet.ResourceResponse)
	 */
	@Override
	protected void doServeResource(ResourceRequest resourceRequest,
			ResourceResponse resourceResponse) throws Exception {
		// TODO Auto-generated method stub
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
		
		Gson gson = new Gson();
		JsonObject infoUrl = new JsonObject();
		
		String infoCot = ParamUtil.getString(resourceRequest, "infoCot");
		String paso = ParamUtil.getString(resourceRequest, "paso");
		InfoCotizacion infCot = gson.fromJson(infoCot, InfoCotizacion.class);
		String url = generaUrl(infCot, resourceRequest, paso);
		if(Validator.isNull(url)){
			infoUrl.addProperty("code", 2);
			infoUrl.addProperty("msg", "Error al redireccionar");
		}else{
			infoUrl.addProperty("code", 0);
			infoUrl.addProperty("msg", url);
		}
		PrintWriter writer = resourceResponse.getWriter();
		writer.write(infoUrl.toString());
	}
	
	/**
	 * 
	 * @param infCot
	 * @param resourceRequest
	 * @param paso recordar que el formato del paso es--> /pasoX, donde la x es al paso donde va 
	 * @return
	 */
	private String generaUrl(InfoCotizacion infCot, ResourceRequest resourceRequest, String paso){
		try {
			HttpServletRequest originalRequest = PortalUtil
					.getOriginalServletRequest(PortalUtil.getHttpServletRequest(resourceRequest));
			ThemeDisplay themeDisplay = (ThemeDisplay) resourceRequest
					.getAttribute(WebKeys.THEME_DISPLAY);
			String parametro = "?infoCotizacion=" + CotizadorModularUtil.encodeURL(infCot);
			final long GROUP_ID = themeDisplay.getLayout().getGroupId();
			Layout layout = LayoutLocalServiceUtil.getFriendlyURLLayout(GROUP_ID, true, paso);
			String urlCotizador = layout.getRegularURL(originalRequest);
			
			
			return (urlCotizador + parametro);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return "";
		}
		
		
	}

}
