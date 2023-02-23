package com.tokio.paso1.portlet;

import com.google.gson.Gson;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.tokio.cotizadorModular.Bean.SimpleResponse;
import com.tokio.cotizadorModular.Interface.CotizadorGenerico;
import com.tokio.paso1.constants.CotizadorPaso1PortletKeys;

import java.io.PrintWriter;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + CotizadorPaso1PortletKeys.PORTLET_NAME,
		"mvc.command.name=/cotizadores/validaAgente"
	},
	service = MVCResourceCommand.class
)

public class ValidaAgenteResourceCommand extends BaseMVCResourceCommand {
	
	@Reference
	CotizadorGenerico _CMGenerico;
	
	
	@Override
	protected void doServeResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
			throws Exception {
		
		String auxCodigo = ParamUtil.getString(resourceRequest, "codigoAgente");
		int cotizacion = ParamUtil.getInteger(resourceRequest, "cotizacion");
		String codigoAgente = "";
		
		if(!auxCodigo.isEmpty()) {
			String auxCodAgente[] = auxCodigo.split("-");
			codigoAgente = auxCodAgente[0].trim();
		}
		
		SimpleResponse response = _CMGenerico.validaAgentes(cotizacion, codigoAgente,
				CotizadorPaso1PortletKeys.PANTALLA_EMPRESARIAL);
		
		Gson gson = new Gson();
		String jsonString = gson.toJson(response);
		PrintWriter writer = resourceResponse.getWriter();
		writer.write(jsonString);
		
	}

}
