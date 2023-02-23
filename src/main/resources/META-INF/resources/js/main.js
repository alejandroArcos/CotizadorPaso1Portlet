$( document ).ready(function() {
	showLoader();	
	window.scrollTo(0, 0);
	
	/*
	var retroactivo = new Date();
	if(!valIsNullOrEmpty(esRetroactivo)){
		if(esRetroactivo){
			retroactivo = -14;
		}
	}
	*/
	
	var retroactivo;
	
	if(diasRetro > 0) {
		if(diasRetro == 90) {
			retroactivo = new Date();
			retroactivo.setMonth(retroactivo.getMonth()-3);
		}
		else {
			retroactivo = -diasRetro;
		}
	}
	else{
		retroactivo = new Date();
	}
	 
	
	$( '.datepicker' ).pickadate( {
		format : 'yyyy-mm-dd', /* 'dd/mm/yyyy', */
		formatSubmit : 'yyyy-mm-dd',
		min : retroactivo,
		max : 365
	} );
	
	$( "#ce_nombre" ).autocomplete( {
		minLength : 3,
		source : function(request, response) {
			$.getJSON( ligasServicios.listaPersonas, {
				term : request.term,
				tipo : 1,
				pantalla : infCotizacion.pantalla
			}, function(data, status, xhr) {
				sessionExtend();
				if (data.codigo == '0') {
					showMessageError( '.navbar', msj.es.errorInformacion, 0 );
					console.error("autocomplete nombre");
					response( null );
				} else {
					response( data );
				}
			} );
		},
		focus : function(event, ui) {
			$( "#ce_nombre" ).val( ui.item.nombrepersona );
			return false;
		},
		select : function(event, ui) {
			$( "#ce_nombre" ).val( ui.item.nombre + " " + ui.item.appPaterno + " " + ui.item.appMaterno );
			$( "#ce_rfc" ).val( ui.item.rfc );
			$( "#ce_codigo" ).val( ui.item.codigo );
			$( "#ce_idPersona" ).val( ui.item.idPersona );
			$( "#tipoPer" ).val( ui.item.tipoPer );
			$( "#idDenominacion" ).val( ui.item.idDenominacion );
			activaCampos("#ce_rfc");
			activaCampos("#ce_codigo");
			auxP1.infoClientExistenttEncontrado = ui.item;
			seleccionaTipoPer();
			return false;
		},
		error : function(jqXHR, textStatus, errorThrown) {
			showMessageError( '.navbar', msj.es.errorInformacion, 0 );
			console.error("autocomplete nombre");
		}
	} ).autocomplete( "instance" )._renderItem = function(ul, item) {
		if (item.idDenominacion == 0) {
			return $( "<li>" ).append(
					"<div>" + item.nombre + " " + item.appPaterno + " " + item.appMaterno + "</div>" )
					.appendTo( ul );
		} else {
			return $( "<li>" ).append(
					"<div>" + item.nombre + " " + item.appPaterno + " " + item.appMaterno + "</div>" )
					.appendTo( ul );
		}
	};

	/* autocomplite por rfc */
	$( "#ce_rfc" ).autocomplete( {
		minLength : 3,
		source : function(request, response) {
			$.getJSON( ligasServicios.listaPersonas, {
				term : request.term,
				tipo : 3,
				pantalla : infCotizacion.pantalla
			}, function(data, status, xhr) {
				sessionExtend();
				if (data.codigo == '0') {
					showMessageError( '.navbar', msj.es.errorInformacion, 0 );
					console.error("autocomplete rfc");
					response( null );
				} else {
					response( data );
				}
			} );
		},
		focus : function(event, ui) {
			$( "#ce_rfc" ).val( ui.item.nombrepersona );
			return false;
		},
		select : function(event, ui) {
			$( "#ce_nombre" ).val( ui.item.nombre + " " + ui.item.appPaterno + " " + ui.item.appMaterno );
			$( "#ce_rfc" ).val( ui.item.rfc );
			$( "#ce_codigo" ).val( ui.item.codigo );
			$( "#ce_idPersona" ).val( ui.item.idPersona );
			$( "#tipoPer" ).val( ui.item.tipoPer );
			$( "#idDenominacion" ).val( ui.item.idDenominacion );
			activaCampos("#ce_nombre");
			activaCampos("#ce_codigo");
			auxP1.infoClientExistenttEncontrado = ui.item;
			seleccionaTipoPer();
			return false;
		},
		error : function(jqXHR, textStatus, errorThrown) {
			showMessageError( '.navbar', msj.es.errorInformacion, 0 );
			console.error("autocomplete rfc");
		}
	} ).autocomplete( "instance" )._renderItem = function(ul, item) {
		if (item.idDenominacion == 0) {
			return $( "<li>" ).append(
					"<div>" + item.rfc + " - " + item.nombre + " " + item.appPaterno + " " + item.appMaterno
							+ "</div>" ).appendTo( ul );
		} else {
			return $( "<li>" ).append(
					"<div>" + item.rfc + " - " + item.nombre + " " + item.appPaterno + " " + item.appMaterno
							+ "</div>" ).appendTo( ul );
		}
	};

	aplicaReglas();
	fValidaAgente();
	hideLoader();
});

function aplicaReglas() {
	seleccionaTipoCotizacion();
	seleccionaModo();
}

function seleccionaModo() {
	switch (infCotizacion.modo) {
		case modo.NUEVA :
			ocultaCampos( ".divFolio" );
			break;
		case modo.EDICION :
			if(datosCliente != ''){
				auxP1.infoClientExistenttEncontrado = JSON.parse(datosCliente);
				bloqueaCampoEdicion();
			}else{
				showMessageError('.navbar', msj.es.errorInformacion, 0);
				console.error(" Modo Edicion");
			} 
			break;
		case modo.COPIA :
			if(datosCliente == ''){
				showMessageError('.navbar', msj.es.errorInformacion, 0);
				console.error(" Modo copia");
			}else{
				auxP1.infoClientExistenttEncontrado = JSON.parse(datosCliente);
				$("#dc_subgiro").prop("disabled", false);
			}
			break;
		case modo.ALTA_ENDOSO :
			auxP1.infoClientExistenttEncontrado = JSON.parse(datosCliente);
			$('#dc_dateDesde').off("change");
			bloqueaCampoAltaEndoso();
			
			break;
		case modo.EDITAR_ALTA_ENDOSO :
			auxP1.infoClientExistenttEncontrado = JSON.parse(datosCliente);
			$('#dc_dateDesde').off("change");
			bloqueaCampoAltaEndoso();
			break;
		case modo.CONSULTA :
			auxP1.infoClientExistenttEncontrado = JSON.parse(datosCliente);
			bloqueaCamposConsulta();
			break;
		case modo.CONSULTAR_REVISION :
			auxP1.infoClientExistenttEncontrado = JSON.parse(datosCliente);
			bloqueaCamposConsulta();
			break;
		case modo.BAJA_ENDOSO :
			auxP1.infoClientExistenttEncontrado = JSON.parse(datosCliente);
			$('#dc_dateDesde').off("change");
			bloqueaCampoBajaEndoso();
			break;
		case modo.EDITAR_BAJA_ENDOSO :
			auxP1.infoClientExistenttEncontrado = JSON.parse(datosCliente);
			$('#dc_dateDesde').off("change");
			bloqueaCampoBajaEndoso();
			break;
		case modo.FACTURA_492 :
			showLoader();
			ocultaCampos( "#paso1_next" );
			auxP1.infoClientExistenttEncontrado = JSON.parse(datosCliente);
			bloqueaCamposConsulta();
			redirigePaso3();
			break;
		case modo.EDITAR_RENOVACION_AUTOMATICA:
			if(datosCliente != ''){
				auxP1.infoClientExistenttEncontrado = JSON.parse(datosCliente);
				bloqueaCampoEdicion();
			}else{
				showMessageError('.navbar', msj.es.errorInformacion, 0);
				console.error(" Modo Edicion");
			}
			break;
		default:
			showMessageError('.navbar', msj.es.errorInformacion, 0);
			console.error(" Modo default");
			break;
	}
}

function seleccionaTipoCotizacion() {
	switch (infCotizacion.tipoCotizacion) {
		case tipoCotizacion.FAMILIAR:
			ocultaCampos( ".empresarial_giros" );
			ocultaCampos( $("#dc_detalleSubgiro").parent() );
			break;
		case tipoCotizacion.EMPRESARIAL:
			muestraCampos( ".empresarial_giros" );
			muestraCampos( $("#dc_detalleSubgiro").parent() );
			break;
		default:
			ocultaCampos( "#paso1_next" );
	}
}

function bloqueaRadio(){
	$(".divRdoVigencia .form-check-input[value = " + infVigencia +"]").trigger("click")
	deshabilitaRadio(".divRdoTpClient", true);
	deshabilitaRadio(".divRdoVigencia", true);
	
}

function bloqueaCampoEdicion(){
	bloqueaRadio();
	$("#ce_rfc").prop("disabled", true);
	$("#ce_nombre").prop("disabled", true);
	$("#ce_codigo").prop("disabled", true);
	$("#dc_movimientos").prop("disabled", true);
	$("#dc_dateDesde").prop("disabled", true);
	$("#dc_agentes").prop("disabled", true);
	$("#dc_giro").prop("disabled", true);
	$("#dc_subgiro").prop("disabled", true);
	$("#dc_detalleSubgiro").prop("disabled", true);
	activaMsjMoneda();
} 

function bloqueaCampoAltaEndoso(){
	bloqueaRadio();
	$("#ce_rfc").prop("disabled", true);
	$("#ce_nombre").prop("disabled", true);
	$("#ce_codigo").prop("disabled", true);
	$("#dc_movimientos").prop("disabled", true);
	$("#dc_agentes").prop("disabled", true);
	$("#dc_moneda").prop("disabled", true);
	$("#dc_formpago").prop("disabled", true);
	$("#dc_giro").prop("disabled", true);
	$("#dc_subgiro").prop("disabled", true);
	$("#dc_detalleSubgiro").prop("disabled", true);
	$(".divRdoVigencia").addClass("d-none");
	
	$("#dc_dateHasta").prop("disabled", true);
	var pick_fin = $( '#dc_dateHasta' ).pickadate( 'picker' );
	var fin = pick_fin.get("select");
	pick_fin.set('max', fin.obj);
} 


function bloqueaCampoBajaEndoso(){
	bloqueaRadio();
	$("#ce_rfc").prop("disabled", true);
	$("#ce_nombre").prop("disabled", true);
	$("#ce_codigo").prop("disabled", true);
	$("#dc_movimientos").prop("disabled", true);
	$("#dc_agentes").prop("disabled", true);
	$("#dc_moneda").prop("disabled", true);
	$("#dc_formpago").prop("disabled", true);
	$("#dc_giro").prop("disabled", true);
	$("#dc_subgiro").prop("disabled", true);
	$("#dc_detalleSubgiro").prop("disabled", true);
	$(".divRdoVigencia").addClass("d-none");
	
	$("#dc_dateHasta").prop("disabled", true);
	var pick_fin = $( '#dc_dateHasta' ).pickadate( 'picker' );
	var fin = pick_fin.get("select");
	pick_fin.set('max', fin.obj);
} 

function bloqueaCamposConsulta(){
	bloqueaRadio();
	$("#ce_rfc").prop("disabled", true);
	$("#ce_nombre").prop("disabled", true);
	$("#ce_codigo").prop("disabled", true);
	$("#dc_movimientos").prop("disabled", true);
	$("#dc_agentes").prop("disabled", true);
	$("#dc_moneda").prop("disabled", true);
	$("#dc_formpago").prop("disabled", true);
	$("#dc_dateDesde").prop("disabled", true);
	$("#dc_dateHasta").prop("disabled", true);
	$("#dc_giro").prop("disabled", true);
	$("#dc_subgiro").prop("disabled", true);
	$("#dc_detalleSubgiro").prop("disabled", true);
}

function activaMsjMoneda(){
	$("#dc_moneda").on("change", function(){
		showMessageError('.navbar', "Si modifica la moneda de esta cotización el cambio aplicara para todas las versiones", 0);
	});	
}

function desactivaMsjMoneda(){
	$("#dc_moneda").off("change");
}

$('#dc_dateDesde').on("change", function() {
	var pick_ini = $( '#dc_dateDesde' ).pickadate( 'picker' );
	var pick_fin = $( '#dc_dateHasta' ).pickadate( 'picker' );
	var iniSelec = pick_ini.get("select");
	if(valIsNullOrEmpty(iniSelec)){
		pick_fin.set('clear');
	}else{
		var anioSig = new Date((iniSelec.year +1), iniSelec.month, iniSelec.date);
		pick_fin.set('max', anioSig);
		pick_fin.set('select', anioSig);
		pick_fin.set('view', anioSig);		
	}
});

$(".tip_fisica input:text").keyup(function(){
	$("#cn_nombrecompleto").val(
	$("#cn_fisnombre"). val() + " "	+	
	$("#cn_fispaterno"). val() + " " +		
	$("#cn_fismaterno"). val() + "."		
	);
});


/**
 * @description tipo 0 = Cliente Existente, tipo 1 = Cliente Nuevo
 */
$( '.divRdoTpClient .form-check-input' ).click( function(e) {
	if ($( this ).val() == "1") {
		muestraCampos( ".data_ctenvo" );
		ocultaCampos( ".data_cteext" );
		$(".data_cteext input:text").val("");
	} else {
		muestraCampos( ".data_cteext" );
		ocultaCampos( ".data_ctenvo" );
	}
} );

/**
 * @description tipo 2 = persona moral, tipo 1 = persona fisica
 */
$( '.tipo_persona .form-check-input' ).click( function() {
	if ($( this ).val() == "1") {
		muestraCampos( ".tip_fisica" );
		ocultaCampos( ".tip_moral" );
		fLlenaNombreFisica();
		actualisaFisica();
	} else {
		muestraCampos( ".tip_moral" );
		ocultaCampos( ".tip_fisica" );
		fLlenaNombreMoral();
		actualizaMoral();
	}
} );

function actualisaFisica(){
	$(".cn_ncEx").addClass("col-md-6");
	$(".cn_ncEx").removeClass("col-md-8");
	$(".cn_tpEx").addClass("col-md-3");
	$(".cn_tpEx").removeClass("col-md-4");
	$(".cn_rdEx").removeClass("d-none");
}

function actualizaMoral(){
	$(".cn_ncEx").addClass("col-md-8");
	$(".cn_ncEx").removeClass("col-md-6");
	$(".cn_tpEx").addClass("col-md-4");
	$(".cn_tpEx").removeClass("col-md-3");
	$(".cn_rdEx").addClass("d-none");
}

$(".cn_rdEx .switch #chktoggle").change(function(){
	if($(".cn_rdEx .switch #chktoggle").is(":checked")){
		$("#cn_fismaterno").removeClass("valExistt");
	}else{
		$("#cn_fismaterno").addClass("valExistt");
	}
});


$( '.tip_fisica .tip_fisica_llena' ).keyup( function() {
	fLlenaNombreFisica();
} );

$( '.divPerMor #cn_nombrecontratante' ).keyup( function() {
	fLlenaNombreMoral();
} );

$( '.divPerMor #cn_denominacion' ).on( 'change', function() {
	fLlenaNombreMoral();
} );

function fLlenaNombreFisica() {
	var apPat = $( "#cn_fispaterno" ).val();
	var apMat = $( "#cn_fismaterno" ).val();
	var nom = $( "#cn_fisnombre" ).val();
	$( "#cn_nombrecompleto" ).val( nom + " " + apPat + " " + apMat );
	activaCampos( "#cn_nombrecompleto" );
}

function fLlenaNombreMoral() {
	var nom = ($( "#cn_nombrecontratante" ).val().length > 0) ? ($( "#cn_nombrecontratante" ).val() + ", ") : "";
	var tip = ($( "#cn_denominacion :selected" ).val() != '-1') ? $( "#cn_denominacion :selected" ).text() : "";
	$( "#cn_nombrecompleto" ).val( nom + tip );
	activaCampos( "#cn_nombrecompleto" );
}

$( '#cn_rfc' ).on('blur',function(e) {
	if(!valIsNullOrEmpty($(this).val())){
		showLoader();
		auxP1.infoClientExistenttEncontrado = null;
		var rfc = $( "#cn_rfc" ).val().toUpperCase();
		if (rfcGenerico.indexOf( rfc ) < 0) {
			$.post( ligasServicios.listaPersonas, {
				term : $( this ).val(),
				tipo : 3,
				pantalla : infCotizacion.pantalla
			}, function(data) {
				var response = jQuery.parseJSON( data );
				if (response.length > 0) {
					$.each( response, function(key, registro) {
						if (registro.rfc === rfc) {
							auxP1.infoClientExistenttEncontrado = registro;
							return false;
						}
					} );
					if (auxP1.infoClientExistenttEncontrado != null) {
						$( '#nombreClienteExistt' ).text(
								auxP1.infoClientExistenttEncontrado.rfc + ' - ' + auxP1.infoClientExistenttEncontrado.nombre
										+ ' ' + auxP1.infoClientExistenttEncontrado.appPaterno + ' '
										+ auxP1.infoClientExistenttEncontrado.appMaterno );
						$( '#modalClienteExistente' ).modal( 'show' );
					}
				}else{
					auxP1.infoClientExistenttEncontrado = null;
				}
				hideLoader();
			} );
		}else{
			hideLoader();
		}
	}
} );

$( '#modalClienteExistente' ).on( 'hidden.bs.modal', function() {
	$(".data_ctenvo .form-control:input:text").val("");
} );

$( '#btnClienttExisttSi' ).click(function() {
	clickButton = true;
	$( '#radio_ce' ).trigger( 'click' );
	$( "#ce_nombre" ).val(
			auxP1.infoClientExistenttEncontrado.nombre + " " + auxP1.infoClientExistenttEncontrado.appPaterno + " "
					+ auxP1.infoClientExistenttEncontrado.appMaterno );
	$( "#ce_rfc" ).val( auxP1.infoClientExistenttEncontrado.rfc );
	$( "#ce_codigo" ).val( auxP1.infoClientExistenttEncontrado.codigo );
	$(".data_ctenvo .form-control:input:text").val("");
	
} );


/**
 * @descripcion val = 0 con vigencia anual,
 * val = 1 --> a partir de emision
 */
$(".divRdoVigencia .form-check-input").click(function() {
    if ($(this).val() == "1") {
    	ocultaCampos($("#dc_dateDesde").parent());
    	ocultaCampos($("#dc_dateHasta").parent());
    }else{
    	muestraCampos($("#dc_dateDesde").parent());
    	muestraCampos($("#dc_dateHasta").parent());
    }
});


$('#dc_giro').change(function() {
    showLoader();
    $("#dc_subgiro option:not(:first)").remove();
    
    if ($(this).val() === '-1') {
    	 selectDestroy($("#dc_subgiro"), true);
         hideLoader();
    }else{
    	var seleccionado = $(this).val();
        $.post(ligasServicios.listaSubgiros, {
            giro: seleccionado,
            pantalla : infCotizacion.pantalla
        }).done(function(data) {
            sessionExtend();
            if (valIsNullOrEmpty(data)) {
                showMessageError('.navbar', msj.es.errorInformacion, 0);
                console.error("giro change");
                selectDestroy($("#dc_subgiro"), true);
            } else {
                var response = jQuery.parseJSON(data);
                if(response.totalRow > 0){
                	$.each(response.lista, function(key, registro) {
                        $("#dc_subgiro").append(
                            "<option value=\"" + registro.idCatalogoDetalle + "\" suscripcion=\"" +
                            registro.otro + "\" >" + registro.valor + "</option>");
                    });
                    selectDestroy($("#dc_subgiro"), false);
                }else{
                	$("#dc_subgiro option:not(:first)").remove();
                	selectDestroy($("#dc_subgiro"), true);
                	showMessageError('.navbar', msj.es.errorInformacion, 0);
                	console.error("giro change");
                }
            }
            hideLoader();
        }).fail(function() {
        	$("#dc_subgiro option:not(:first)").remove();
        	selectDestroy($("#dc_subgiro"), true);
        	showMessageError('.navbar', msj.es.errorInformacion, 0);
        	console.error("giro change");
        	 hideLoader();
        });
    }
});


$("#paso1_next").click(function(e){
	removeClassInvalid();
	showLoader()
	
	var completos = validaRequeridos();
	if(completos){
		llenaDatos();
		guardaPaso1();
	}else{
		hideLoader();
		showMessageError('.navbar', msj.es.faltaInfo, 0);
		console.error("paso 1 next");
	}
});

function seleccionaTipoPer(){
	if (auxP1.infoClientExistenttEncontrado.tipoPer == 217){
		infCotizacion.tipoPersona = tipoPersona.FISICA;
	}else if(auxP1.infoClientExistenttEncontrado.tipoPer == 218){
		infCotizacion.tipoPersona = tipoPersona.MORAL;
	}
}

function validaRequeridos(){
	var campos = $("#contPaso1 input:visible:enabled:not(:radio)");
	var completos = true;
	if( $(".cn_rdEx .switch:visible #chktoggle").is(":checked")){
		campos = $("#contPaso1 input:visible:enabled:not(:radio)").not("#cn_fismaterno");
	}
	$.each(campos, function(key, campo) {
		if($(campo).hasClass("select-dropdown")){
			var select = $(campo).siblings("select");
			completos = noSelect($(select)) ? false : completos;
		}else{
			completos = vaciosInpText($(campo)) ? false : completos;	
		}	
	});
	return completos;
}

function llenaDatos(){
	if($("#radio_ce").is(":checked")){
		llenaClienteExistente();
	}else{
		llenaClienteNuevo();
	}
	llenaDatsGene();
}

function llenaClienteExistente(){
	DatosGenerales.idPersona = auxP1.infoClientExistenttEncontrado.idPersona;
	DatosGenerales.tipoPer = auxP1.infoClientExistenttEncontrado.tipoPer;
	DatosGenerales.rfc = auxP1.infoClientExistenttEncontrado.rfc;
	DatosGenerales.nombre = auxP1.infoClientExistenttEncontrado.nombre;
	DatosGenerales.appPaterno = auxP1.infoClientExistenttEncontrado.appPaterno;
	DatosGenerales.appMaterno = auxP1.infoClientExistenttEncontrado.appMaterno;
	DatosGenerales.idDenominacion = auxP1.infoClientExistenttEncontrado.idDenominacion;
	DatosGenerales.codigo = auxP1.infoClientExistenttEncontrado.codigo;
}

function llenaClienteNuevo(){
	DatosGenerales.idPersona = 0;
	DatosGenerales.tipoPer = parseInt($(".tipo_persona .form-check-input:checked").val(), 10);
	DatosGenerales.rfc = $("#cn_rfc").val();
	llenaNombreCN();
	DatosGenerales.codigo = "";
}

function llenaNombreCN(){
	if($("#cn_personamoral").is(":checked")){
		DatosGenerales.nombre = $("#cn_nombrecontratante").val();
		DatosGenerales.appPaterno ="";
		DatosGenerales.appMaterno = "";
		DatosGenerales.idDenominacion = parseInt($("#cn_denominacion").val(), 10);
		DatosGenerales.extranjero = 0;
	}else{
		DatosGenerales.nombre = $("#cn_fisnombre").val();
		DatosGenerales.appPaterno = $("#cn_fispaterno").val();
		DatosGenerales.appMaterno = $("#cn_fismaterno").val();
		DatosGenerales.idDenominacion = 0;
		DatosGenerales.extranjero = $(".cn_rdEx .switch:visible #chktoggle").is(":checked") ? 1: 0;
	}
}

function llenaDatsGene(){
	DatosGenerales.tipomov = parseInt($("#dc_movimientos").val(), 10);
	llenaTpoVigencia();
	DatosGenerales.moneda = parseInt($("#dc_moneda").val(), 10);
	DatosGenerales.agente = parseInt($("#dc_agentes").val(), 10);
	DatosGenerales.formapago = parseInt($("#dc_formpago").val(), 10);
	DatosGenerales.giro = parseInt($("#dc_giro").val(), 10);
	DatosGenerales.subGiro = parseInt($("#dc_subgiro").val(), 10);
	DatosGenerales.detalleSubGiro = $("#dc_detalleSubgiro").val();
	DatosGenerales.p_permisoSubgiro = $('#dc_subgiro option:selected').attr('suscripcion');
	DatosGenerales.modo = infCotizacion.modo;
	DatosGenerales.tipoCot = infCotizacion.tipoCotizacion;
	DatosGenerales.folio = infCotizacion.folio;
	DatosGenerales.cotizacion = infCotizacion.cotizacion;
	DatosGenerales.version = infCotizacion.version;
	DatosGenerales.pantalla = infCotizacion.pantalla;
	DatosGenerales.subEstado = infsubEstado;	
}

function llenaTpoVigencia(){
	if($("#dc_cotizarVig").is(":checked")){
		DatosGenerales.vigencia =  0;
		DatosGenerales.fecinicio =  $("#dc_dateDesde").val();
		DatosGenerales.fecfin =  $("#dc_dateHasta").val();
	}else{
		DatosGenerales.vigencia =  1;
		DatosGenerales.fecinicio =  "";
		DatosGenerales.fecfin =  "";
	}
}

function guardaPaso1(){
	/*$.get( ligasServicios.guardaInfo, {*/
	$.post( ligasServicios.guardaInfo, {
		datos : JSON.stringify(DatosGenerales),
		infoCot : JSON.stringify(infCotizacion)
	}, function(data) {
		console.log(data);
		var response = JSON.parse(data);
		if(response.code == 0){
			window.location.href = response.url ;
		}else{
			showMessageError( '.navbar', response.msg, 0 );
			hideLoader();
		}
	});
	
}

$('#dc_subgiro').change(function() {
	if(!valIsNullOrEmpty(perfilSuscriptor)){
		if(perfilSuscriptor == '0'){
			if($('option:selected', this).attr('suscripcion') == '1'){
				$('#modalGiroSubgiro').modal({
	                show: true,
	                backdrop: 'static',
	                keyboard: false
	            });
			}
		}
	}
});


$("#btnSuscripGiroNo").click(function(){
	$('#dc_subgiro option[value=-1]').prop('selected', true);
    selectDestroy($('#dc_subgiro'), false);
    $('#modalGiroSubgiro').modal('hide');
});

function redirigePaso3(){
	
	llenaDatos();
	$.post( ligasServicios.guardaInfo, {
		datos : JSON.stringify(DatosGenerales),
		infoCot : JSON.stringify(infCotizacion)
	}, function(data) {
		console.log(data);
		$.post(ligasServicios.redirige, {
			infoCot : JSON.stringify( infCotizacion ),
			paso : "/paso3"
	    }).done(function(data) {
	        sessionExtend();
	        var response = JSON.parse( data );
			if (response.code == 0) {
				window.location.href = response.msg;
			} else {
				showMessageError( '.navbar', response.msg, 0 );
				hideLoader();
			}
	    });
	});
	
	
	
}

$("#dc_agentes").change( function(){
	fValidaAgente();
});

function fValidaAgente(){
	showLoader();
	
	$.post(validaAgenteURL, {
		cotizacion: 0,
    	codigoAgente: $("#dc_agentes option:selected").text()
    }).done(function(data) {
    	
    	var response = JSON.parse(data);
    	
    	if(response.code != 0) {
    		if(response.code == 3) {
    			$("#modalBloqueoAgente").modal('show');
    		}
    		else {
    			showMessageError('.navbar', response.msg, 0);
    		}
    	}
    	
    	hideLoader();
    });
}