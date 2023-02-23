<%@ include file="./init.jsp"%>
<jsp:include page="modales.jsp" />

<portlet:resourceURL id="/cotizadores/paso1/listaPersonas" var="listaPersonasURL" />
<portlet:resourceURL id="/cotizadores/paso1/getSubGiro" var="getSubGiroURL" />
<portlet:resourceURL id="/cotizadores/paso1/guardaPaso" var="guardaPaso1URL" />
<portlet:resourceURL id="/cotizadores/paso1/redirigePasoX" var="redirigeURL" />

<portlet:resourceURL id="/cotizadores/validaAgente" var="validaAgenteURL" cacheability="FULL" />

<c:set var="version" scope="session" value="DES.20220911.1205" />

<link rel="stylesheet" href="<%=request.getContextPath()%>/css/main.css?v=${version}">
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/jquery-ui.css?v=${version}">

<section id="cotizadores-p1" class="upper-case-all">

	<div class="section-heading">
		<div class="container-fluid">
			<h4 class="title text-left">${tituloCotizador}</h4>
		</div>
	</div>
	<div class="container-fluid">
		<div class="row">
			<div class="col-md-12">
				<ul class="stepper stepper-horizontal container-fluid">
					<li id="step1" class="active ">
						<a href="javascript:void(0)">
							<span class="circle">1</span>
							<span class="label">
								<liferay-ui:message key="CotizadorPaso1Portlet.titPasoUno" />
							</span>
						</a>
					</li>
					<li id="step2">
						<a href="javascript:void(0)">
							<span class="circle">2</span>
							<span class="label">
								<liferay-ui:message key="CotizadorPaso1Portlet.titPasoDos" />
							</span>
						</a>
					</li>
					<li id="step3">
						<a href="javascript:void(0)">
							<span class="circle">3</span>
							<span class="label">
								<liferay-ui:message key="CotizadorPaso1Portlet.titPasoTres" />
							</span>
						</a>
					</li>
					<li id="step4">
						<a href="javascript:void(0)">
							<span class="circle">4</span>
							<span class="label">
								<liferay-ui:message key="CotizadorPaso1Portlet.titPasoCuatro" />
							</span>
						</a>
					</li>
				</ul>

			</div>
		</div>
	</div>

	<div style="position: relative;">
		<liferay-ui:success key="consultaExitosa" message="CotizadorPaso1Portlet.exito" />
		<liferay-ui:error key="errorConocido" message="${errorMsg}" />
		<liferay-ui:error key="errorDesconocido" message="CotizadorPaso1Portlet.erorDesconocido" />
	</div>

	<div class="container-fluid" id="divPaso1">
		<div class="row divFolio">
			<div class="col-md-9"></div>
			<div class="col-md-3" style="text-align: right;">
				<div class="md-form form-group">
					<input id="txtFolioP1" type="text" name="txtFolioP1" class="form-control" value="${ inf.folio } - ${ inf.version }" disabled>
					<label class="active" for="txtFolioP1">
						<liferay-ui:message key="CotizadorPaso1Portlet.titFolio" />
					</label>
				</div>
			</div>
		</div>
	</div>

	<div class="padding70" id="contPaso1">
		<h5>
			<liferay-ui:message key="CotizadorPaso1Portlet.titDatContratante" />
		</h5>
		<br />

		<div class="row">
			<div class="col-md-12">
				<div class="form-inline divRdoTpClient">
					<div class="form-check">
						<input class="form-check-input form-control" name="group1" type="radio" id="radio_ce" value="0" checked="checked">
						<label class="form-check-label" for="radio_ce">
							<liferay-ui:message key="CotizadorPaso1Portlet.rdbCliExP1" />
						</label>
					</div>
					<div class="form-check">
						<input class="form-check-input form-control" name="group1" type="radio" id="radio_cn" value="1">
						<label class="form-check-label" for="radio_cn">
							<liferay-ui:message key="CotizadorPaso1Portlet.rdbCliNuP1" />
						</label>
					</div>
				</div>
			</div>
		</div>

		<div class="row data_cteext">
			<div class="col-md-3">
				<div class="md-form">
					<input type="text" name="ce_rfc" id="ce_rfc" class="form-control " maxlength="13" pattern="^[a-zA-Z0-9]{4,10}$" value="${ cotizadorData.datosCliente.rfc }">
					<label for="ce_rfc">
						<liferay-ui:message key="CotizadorPaso1Portlet.lblRfcExP1" />
					</label>
				</div>
			</div>
			<div class="col-md-6">
				<div class="md-form">
					<input type="text" name="ce_nombre" id="ce_nombre" class="form-control "  value="${ cotizadorData.datosCliente.nombre } ${ cotizadorData.datosCliente.appPaterno} ${ cotizadorData.datosCliente.appMaterno }">
					<label for="ce_nombre">
						<liferay-ui:message key="CotizadorPaso1Portlet.lblNomComExP1" />
					</label>
				</div>
			</div>
			<div class="col-md-3">
				<div class="md-form">
					<input type="text" name="ce_codigo" id="ce_codigo" class="form-control" value="${ cotizadorData.datosCliente.codigo }"  disabled>
					<label for="ce_codigo">
						<liferay-ui:message key="CotizadorPaso1Portlet.lblCodClieExP1" />
					</label>
				</div>
			</div>
		</div>


		<div class="row data_ctenvo d-none">
			<div class="col-sm-12">
				<div class="row data_nuevotip">
					<div class="col-md-8 cn_ncEx">
						<div class="md-form form-group">
							<input type="text" id="cn_nombrecompleto" name="cn_nombrecompleto" class="form-control" disabled>
							<label for="cn_nombrecompleto">
								<liferay-ui:message key="CotizadorPaso1Portlet.lblNomComExP1" />
							</label>
						</div>
					</div>
					<div class="col-md-4 cn_tpEx">
						<div class="form-inline tipo_persona">
							<div class="form-check">
								<input class="form-check-input form-control" name="group2" type="radio" id="cn_personamoral" checked="checked" value="2">
								<label class="form-check-label" for="cn_personamoral">
									<liferay-ui:message key="CotizadorPaso1Portlet.lblTipPerNvMorP1" />
								</label>
							</div>
							<div class="form-check">
								<input class="form-check-input form-control" name="group2" type="radio" id="cn_personafisica" value="1">
								<label class="form-check-label" for="cn_personafisica">
									<liferay-ui:message key="CotizadorPaso1Portlet.lblTipPerNvFisP1" />
								</label>
							</div>
						</div>
					</div>

					<div class="col-md-3 cn_rdEx d-none">
						<div class="row row justify-content-md-center">
							<label class="pb-2"> Extranjera:</label>
						</div>
						<div class="row row justify-content-md-center">
							<div class="switch">
								<label>
									No
									<input id="chktoggle" type="checkbox">
									<span class="lever"></span>
									Si
								</label>
							</div>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col-md-3">
						<div class="md-form">
							<input type="text" id="cn_rfc" name="cn_rfc " class="form-control " maxlength="13" pattern="^[a-zA-Z0-9]{4,10}$">
							<label for="cn_rfc">
								<liferay-ui:message key="CotizadorPaso1Portlet.lblRfcExP1" />
							</label>
						</div>
					</div>

					<div class="col-md-9 px-0 tip_moral divPerMor">
						<div class="col-md-6">
							<div class="md-form">
								<input type="text" id="cn_nombrecontratante" name="cn_nombrecontratante" class="form-control">
								<label for="cn_nombrecontratante">
									<liferay-ui:message key="CotizadorPaso1Portlet.lblNomConNvMoP1" />
								</label>
							</div>
						</div>
						<div class="col-md-6">
							<div class="md-form form-group">
								<select name="cn_denominacion" id="cn_denominacion" class="mdb-select form-control-sel colorful-select dropdown-primary" searchable='<liferay-ui:message key="CotizadorPaso1Portlet.buscar" />'>
									<option value="-1" selected><liferay-ui:message key="CotizadorPaso1Portlet.selectOpDefoult" /></option>
									<c:forEach items="${listaCatDenominacion}" var="option">
										<option value="${option.idCatalogoDetalle}">${option.valor}</option>
									</c:forEach>
								</select>
								<label for="cn_denominacion">
									<liferay-ui:message key="CotizadorPaso1Portlet.lblDenominaNvMoP1" />
								</label>
							</div>
						</div>
					</div>



					<div class="col-md-9 px-0 tip_fisica" style="display: none">

						<div class="col-md-4">
							<div class="md-form">
								<input type="text" id="cn_fisnombre" name="cn_fisnombre" class="form-control">
								<label for="cn_fisnombre">
									<liferay-ui:message key="CotizadorPaso1Portlet.lblNomFisicaP1" />
								</label>
							</div>
						</div>
						<div class="col-md-4">
							<div class="md-form">
								<input type="text" id="cn_fispaterno" name="cn_fispaterno" class="form-control ">
								<label for="cn_fispaterno">
									<liferay-ui:message key="CotizadorPaso1Portlet.lblApPatFisicaP1" />
								</label>
							</div>
						</div>
						<div class="col-md-4">
							<div class="md-form">
								<input type="text" id="cn_fismaterno" name="cn_fismaterno" class="form-control ">
								<label for="cn_fismaterno">
									<liferay-ui:message key="CotizadorPaso1Portlet.lblApMatFisicaP1" />
								</label>
							</div>
						</div>
					</div>






				</div>


			</div>
		</div>

		<div class="data_cotizacion">
			<br />
			<h5>
				<liferay-ui:message key="CotizadorPaso1Portlet.titDatosCotizacion" />
			</h5>
			<br />
			<div class="row">
				<div class="col-md-12">
					<div class="form-inline form-right float-right divRdoVigencia">
						<div class="form-check">
							<input class="form-check-input form-control" name="group3" type="radio" id="dc_cotizarVig" checked="checked" value="0">
							<label class="form-check-label" for="dc_cotizarVig">
								<liferay-ui:message key="CotizadorPaso1Portlet.rdbCotVigDtsCotizaP1" />
							</label>
						</div>
						<div class="form-check">
							<input class="form-check-input form-control" name="group3" type="radio" id="dc_vigenAnual" value="1">
							<label class="form-check-label" for="dc_vigenAnual">
								<liferay-ui:message key="CotizadorPaso1Portlet.rdbVigAnualDtsCotizaP1" />
							</label>
						</div>
					</div>
				</div>
			</div>

			<div class="row">
				<div class="col-sm-3">
					<div class="md-form form-group">
						<select name="dc_movimientos" id="dc_movimientos" class="mdb-select form-control-sel colorful-select dropdown-primary">
							<option value="-1" selected><liferay-ui:message key="CotizadorPaso1Portlet.selectOpDefoult" /></option>
							<c:forEach items="${listaMovimiento}" var="option">
								<option value="${option.idCatalogoDetalle}" 
								${ cotizadorData.tipoMov ==  option.idCatalogoDetalle ? 'selected' : ''}>
								${option.valor}</option>
							</c:forEach>
						</select>
						<label for="dc_movimientos">
							<liferay-ui:message key="CotizadorPaso1Portlet.lblTipoMovimientoDtsCotizaP1" />
						</label>
					</div>
				</div>
				<div class="col-sm-3">
					<div class="md-form form-group">
						<select name="dc_moneda" id="dc_moneda" class="mdb-select form-control-sel">
							<option value="-1" selected><liferay-ui:message key="CotizadorPaso1Portlet.selectOpDefoult" /></option>
							<c:forEach items="${listaCatMoneda}" var="option">
								<option value="${option.idCatalogoDetalle}" 
								${ cotizadorData.moneda ==  option.idCatalogoDetalle ? 'selected' : ''}>
								${option.valor}</option>
							</c:forEach>
						</select>
						<label for="dc_moneda">
							<liferay-ui:message key="CotizadorPaso1Portlet.lblMonedaDtsCotizaP1" />
						</label>
					</div>
				</div>
				<div class="col-sm-6">
					<div class="md-form form-group">
						<div class="row">
							<div class="col">
								<input placeholder="Fecha Desde" type="date" id="dc_dateDesde" name="dc_dateDesde" class="form-control datepicker " value="${ fechaHoy }" >
								<label for="dc_dateDesde">
									<liferay-ui:message key="CotizadorPaso1Portlet.lblDesdeDtsCotizaP1" />
								</label>
							</div>
							<div class="col">
								<input placeholder="Fecha Hasta" type="date" id="dc_dateHasta" name="dc_dateHasta" class="form-control datepicker" value="${ fechaMasAnio }"  disabled>
								<label for="dc_dateHasta">
									<liferay-ui:message key="CotizadorPaso1Portlet.lblHastaDtsCotizaP1" />
								</label>
							</div>
						</div>
					</div>
				</div>
			</div>

			<div class="row">
				<div class="col-sm-3">
					<div class="md-form form-group">
						<select name="dc_agentes" id="dc_agentes" class="mdb-select form-control-sel colorful-select dropdown-primary" searchable='<liferay-ui:message key="CotizadorPaso1Portlet.buscar" />'>
							<c:if test="${fn:length(listaAgentes) gt 1}">
								<option value="-1" selected><liferay-ui:message key="CotizadorPaso1Portlet.selectOpDefoult" /></option>
							</c:if>
							<c:forEach items="${listaAgentes}" var="option">
								<option value="${option.idPersona}" 
								${ cotizadorData.agente ==  option.idPersona ? 'selected' : ''}>
								${option.nombre}${option.appPaterno}${option.appMaterno}</option>
							</c:forEach>
						</select>
						<label for="dc_agentes">
							<liferay-ui:message key="CotizadorPaso1Portlet.lblAgentesDtsCotizaP1" />
						</label>
					</div>
				</div>

				<div class="col-sm-3">
					<div class="md-form form-group">
						<select name="dc_formpago" id="dc_formpago" class="mdb-select form-control-sel ">
							<option value="-1" selected><liferay-ui:message key="CotizadorPaso1Portlet.selectOpDefoult" /></option>
							<c:forEach items="${listaCatFormaPago}" var="option">
								<option value="${option.idCatalogoDetalle}"
								 ${ cotizadorData.formaPago ==  option.idCatalogoDetalle ? 'selected' : ''}>
								 ${option.valor}</option>
							</c:forEach>
						</select>
						<label for="dc_formpago">
							<liferay-ui:message key="CotizadorPaso1Portlet.lblPagoDtsCotizaP1" />
						</label>
					</div>
				</div>
				<div class="col-6 px-0 empresarial_giros">
					<div class="col-sm-6">
						<div class="md-form form-group">
							<select name="dc_giro" id="dc_giro" class="mdb-select form-control-sel ">
								<option value="-1" selected><liferay-ui:message key="CotizadorPaso1Portlet.selectOpDefoult" /></option>
								<c:forEach items="${listaGiros}" var="option">
									<option value="${option.idCatalogoDetalle}"
									 ${ cotizadorData.giro ==  option.idCatalogoDetalle ? 'selected' : ''}>
									 ${option.valor}</option>
								</c:forEach>
							</select>
							<label for="dc_giro">
								<liferay-ui:message key="CotizadorPaso1Portlet.lblGiroDtsCotizaP1" />
							</label>
						</div>
					</div>
					<div class="col-sm-6">
						<div class="md-form form-group">
							
							<select name="dc_subgiro" id="dc_subgiro" class="mdb-select form-control-sel" disabled>
								<option value="-1" selected><liferay-ui:message key="CotizadorPaso1Portlet.selectOpDefoult" /></option>
								<c:forEach items="${listaSubGiro}" var="option">
									<option suscripcion="${option.otro}" value="${option.idCatalogoDetalle}"
									 ${ cotizadorData.subGiro ==  option.idCatalogoDetalle ? 'selected' : ''}>
									 ${option.valor}</option>
								</c:forEach>
							</select>
							<label for="dc_subgiro">
								<liferay-ui:message key="CotizadorPaso1Portlet.lblSubGiroDtsCotizaP1" />
							</label>
						</div>
					</div>
				</div>
			</div>


			<div id="pruebas" class="row">
				<div class="col-md-6"></div>
				<div class="col-md-6" ${perfilMayorEjecutivo ? '' : 'hidden'}>
					<div class="md-form">
						<textarea type="text" id="dc_detalleSubgiro" name="dc_detalleSubgiro" 
						class="md-textarea form-control" rows="1" maxlength="300" 
						style="text-transform: uppercase;">${ cotizadorData.p_detalleSubGiro }</textarea>
						<label for="dc_detalleSubgiro">
							<liferay-ui:message key="CotizadorPaso1Portlet.lblDetalleSubGiroDtsCotizaP1" />
						</label>
					</div>
				</div>
			</div>
			<div class="row">
				<div class="col-sm-12 text-right">
					<a class="btn btn-pink" id="paso1_next">Continuar</a>
				</div>
			</div>
		</div>


	</div>
</section>

<!-- 	Scripts -->

<script src="<%=request.getContextPath()%>/js/jquery-ui.min.js?v=${version}"></script>
<script src="<%=request.getContextPath()%>/js/main.js?v=${version}"></script>
<script src="<%=request.getContextPath()%>/js/objetos.js?v=${version}"></script>
<script src="<%=request.getContextPath()%>/js/funcionesGenericas.js?v=${version}"></script>



<script>

	ligasServicios.listaPersonas = "${listaPersonasURL}";
	ligasServicios.listaSubgiros = "${getSubGiroURL}";
	ligasServicios.guardaInfo = "${guardaPaso1URL}";
	ligasServicios.redirige = "${redirigeURL}";

	var infCotizacion = ${infCotizacionJson};
	var esRetroactivo = ${perfilMayorEjecutivo};	

	var datosCliente = '${datosCliente}';
	var infVigencia = '${cotizadorData.vigencia}';
	var infsubEstado = '${cotizadorData.subEstado}';
	var perfilSuscriptor = '${perfilSuscriptor}';
	
	var diasRetro = ${retroactividad};
	
	var validaAgenteURL = '${validaAgenteURL}';
	
</script>


