/**
 * Agrega clase al campo espesifico para ocultarlo
 * mandar el selector tipo jquery
 * id -> #id
 * class -> .class
 * @param selector
 * @returns
 */
function ocultaCampos(selector){
	$(selector).addClass("d-none");
	$(selector).removeClass("d-block");
}

/**
 * Agrega clase al campo espesifico para mostrarlo
 * mandar el selector tipo jquery
 * id -> #id
 * class -> .class
 * @param selector
 * @returns
 */
function muestraCampos(selector){
	$(selector).addClass("d-block");
	$(selector).removeClass("d-none");
}

function activaCampos(campo){
	if(valIsNullOrEmpty($(campo).val())){
		$(campo).siblings('label').removeClass('active');
	}else{
		$(campo).siblings('label').addClass('active');
	}
}

/**
 * Destrulle y regenera los material select
 * @param objeto
 * @param enabled
 * @returns
 */
function selectDestroy(objeto, enabled) {
    $(objeto).prop("disabled", enabled);
    $(objeto).material_select('destroy');
    $(objeto).material_select();
}


function noSelect(campo) {
    var errores = false;
    if ($(campo).val() == "-1") {
        errores = true;
        $(campo).siblings("input").addClass('invalid');
        $(campo).parent().append(
            "<div class=\"alert alert-danger\"> <span class=\"glyphicon glyphicon-ban-circle\"></span> " + " " +
            msj.es.campoRequerido + "</div>");
    }
    
    return errores;
}

function vaciosInpText(value) {	
	var errores = false;	
	if($(value).is(":visible")){
		if (valIsNullOrEmpty($(value).val())) {
			errores = true;
			$(value).addClass('invalid');
			$(value).parent().append(
					"<div class=\"alert alert-danger\" role=\"alert\"> <span class=\"glyphicon glyphicon-ban-circle\"></span>" + " "
					+ msj.es.campoRequerido + "</div>");
		}			
	}
	return errores;
}


$("#contPaso1 :input").change(function(){
	removeClassInvalid();
});

$("#contPaso1 :input").click(function(){
	removeClassInvalid();
});

function removeClassInvalid(){
	$(".alert-danger").remove();
    $('.invalid').removeClass('invalid');
}

/**
 * llena input tex 
 * @param campo
 * @param valor
 * @param disabled
 * @returns
 */
function llenaCampoText(campo, valor, disabled){
	$(campo).val(valor);
	activaCampos(campo)
	$(campo).prop("disabled", disabled);
}


function valIsNullOrEmpty(value) {
	if (value === undefined) {
		return true;
	}
	value = value.trim();
	return (value == null || value == "null" || value === "");
}


function deshabilitaRadio(selector, disabled){
	$(selector).find(".form-check-input").prop("disabled", disabled);
}

function seleccionaOpcionSelect(campo, value, disabled){
	$(campo + " option[value = '"+ value +"' ]").attr("selected", true)
	selectDestroy(campo, disabled);
}