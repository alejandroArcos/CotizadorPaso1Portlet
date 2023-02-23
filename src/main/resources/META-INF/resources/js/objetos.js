/**
 * Modo para el flujo de cotizaciones
 */
const modo = {
    NUEVA: "NUEVA",
    EDICION: "EDICION",
    COPIA: "COPIA",
    AUX_PASO4: "AUX_PASO4",
    ALTA_ENDOSO: "ALTA_ENDOSO",
    BAJA_ENDOSO: "BAJA_ENDOSO",
    EDITAR_ALTA_ENDOSO : "EDITAR_ALTA_ENDOSO",
    EDITAR_BAJA_ENDOSO : "EDITAR_BAJA_ENDOSO",
    CONSULTA : "CONSULTA",
    CONSULTAR_REVISION : "CONSULTAR_REVISION",
    FACTURA_492 : "FACTURA_492",
	EDITAR_RENOVACION_AUTOMATICA: "EDITAR_RENOVACION_AUTOMATICA"
};


const tipoCotizacion = {
	ERROR : "ERROR",
	FAMILIAR : "FAMILIAR",
	EMPRESARIAL : "EMPRESARIAL"
};

const tipoPersona = {
		FISICA : "FISICA",
		MORAL : "MORAL"
};

const formatter = new Intl.NumberFormat('en-US', {
	  style: 'currency',
	  currency: 'USD',
	  minimumFractionDigits: 2
});

const diasRetroactividad = 14;

const msj = {
		es : {
			errorInformacion : "Error al  cargar la información",
			catSinInfo: "Catalogo sin información",
	        campoRequerido: "El campo es requerido",
	        faltaInfo: "Hace falta información requerida",
	        errorGuardar: "Error al guardar su información"
		}
};

/**
 * objeto para Url de Resources Command 
 */
var ligasServicios = {
	listaPersonas : "",
	listaSubgiros : "",
	guardaInfo : "",
	redirige : ""
};

/**
 * rfc genericos que se descartan
 */
var rfcGenerico = ["XAXX010101000", "XEXX010101000"];

/**
 * Variables globales auxiliares j
 */
var auxP1 = {
	infoClientExistenttEncontrado : null
};

/**
 * informacion necesaria para guardar
 */
var DatosGenerales = {
		 tipomov :  0,
		 vigencia :  0,
		 fecinicio :  "",
		 fecfin :  "",
		 moneda :  0,
		 formapago :  0,
		 agente :  0,
		 idPersona :  0,
		 tipoPer :  0,
		 rfc : ""  ,
		 nombre :  "",
		 appPaterno :  "",
		 appMaterno :  "",
		 idDenominacion :  0,
		 codigo :  "",
		 modo :  "",
		 tipoCot : "",
		 cotizacion :  "",
		 version :  0,
		 giro :  0,
		 subGiro :  0,
		 folio :  "",
		 detalleSubGiro :  "",
		 pantalla : "",
		 p_permisoSubgiro :  0,
		 subEstado : ""
};

