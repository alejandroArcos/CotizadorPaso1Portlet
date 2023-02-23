/**
 * 
 */
package com.tokio.paso1.portlet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.tokio.cotizadorModular.Bean.PersonaResponse;
import com.tokio.cotizadorModular.Interface.CotizadorPaso1;
import com.tokio.paso1.constants.CotizadorPaso1PortletKeys;

import java.io.PrintWriter;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author jonathanfviverosmoreno
 *
 */


@Component(
		immediate = true, property = { 
				"javax.portlet.name=" + CotizadorPaso1PortletKeys.PORTLET_NAME, 
				"mvc.command.name=/cotizadores/paso1/listaPersonas" }, service = MVCResourceCommand.class
)
public class GetListaPersonasResourceCommand extends BaseMVCResourceCommand {
	
	@Reference
	CotizadorPaso1 _CMServicesP1;

	/* (non-Javadoc)
	 * @see com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand#doServeResource(javax.portlet.ResourceRequest, javax.portlet.ResourceResponse)
	 */
	@Override
	protected void doServeResource(ResourceRequest resourceRequest,
			ResourceResponse resourceResponse) throws Exception {
		// TODO Auto-generated method stub
		
		/************************** Validación metodo post **************************/
		if ( !resourceRequest.getMethod().equals("GET")  ){
			JsonObject requestError = new JsonObject();
			requestError.addProperty("code", 500);
			requestError.addProperty("msg", "Error en tipo de consulta");
			PrintWriter writer = resourceResponse.getWriter();
			writer.write(requestError.toString());
			return;
		}
		/************************** Validación metodo post **************************/
		
		User user = (User) resourceRequest.getAttribute(WebKeys.USER);

		String usuario = user.getScreenName();
		String pantalla = ParamUtil.getString(resourceRequest, "pantalla");;
		String nombreCliente = ParamUtil.getString(resourceRequest, "term");
		int tipo = ParamUtil.getInteger(resourceRequest, "tipo");

		Gson gson = new Gson();
		PrintWriter writer = resourceResponse.getWriter();
		try {
			PersonaResponse respuesta = _CMServicesP1.getListaPersonas(nombreCliente, tipo, usuario, pantalla);

			if (respuesta.getCode() == 0) {

				String jsonString = gson.toJson(respuesta.getPersonas());
				writer.write(jsonString);
			} else {
				writer.write("{\"codigo\" : \"0\", \"error\" : \"sin informacion\" }");
			}

		} catch (Exception e) {
			writer.write("{\"codigo\" : \"0\", \"error\" : \"sin informacion\" }");
		}

	}

}
