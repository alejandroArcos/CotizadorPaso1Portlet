<%@ include file="./init.jsp"%>

<!-- Modal usuario existente -->
<div class="modal" id="modalClienteExistente" tabindex="-1" role="dialog" aria-labelledby="clienteExistenttLabel" aria-hidden="true">
	<div class="modal-dialog modal-lg modal-dialog-centered" role="document">
		<div class="modal-content">
			<div class="modal-header orange">
				<h5 class="modal-title" id="clienteExistenttLabel">
					<liferay-ui:message key="CotizadorPaso1Portlet.titModClientExistt" />
				</h5>
				<button type="button" class="close" data-dismiss="modal" aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
			</div>
			<!--Body-->
			<div class="modal-body">

				<div class="row">
					<div class="col-12">
						<h4 class="font-weight-bold">
							<samp id="nombreClienteExistt"></samp>
						</h4>
						<liferay-ui:message key="CotizadorPaso1Portlet.infoModClientExistt" />
					</div>
				</div>
			</div>

			<!--Footer-->
			<div class="modal-footer justify-content-center">
				<div class="row">
					<div class="col-md-6">
						<button class="btn btn-pink waves-effect waves-light" id="btnClienttExisttSi">Si</button>
					</div>
					<div class="col-md-6">
						<button class="btn btn-blue waves-effect waves-light" data-dismiss="modal">No</button>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<!-- END Modal usuario existente -->


<!-- Modal Giro Sub-giro -->
<div class="modal" id="modalGiroSubgiro" tabindex="-1" role="dialog" aria-labelledby="modalGiroSubgiroLabel" aria-hidden="true">
	<div class="modal-dialog modal-lg modal-dialog-centered" role="document">
		<div class="modal-content">
			<div class="modal-header orange">
				<h5 class="modal-title text-black-50" id="modalGiroSubgiroLabel">
					<i class="fas fa-exclamation-triangle"></i> Alerta de suscripción
				</h5>
			</div>
			<!--Body-->
			<div class="modal-body">

				<div class="row">
					<div class="col-12">
						El Sub Giro seleccionado sólo puede ser cotizado por el área de suscripción <br>
						<h5>¿Desea Continuar?</h5>
					</div>
				</div>
			</div>

			<!--Footer-->
			<div class="modal-footer justify-content-center">
				<div class="row">
					<div class="col-md-6">
						<button class="btn btn-pink waves-effect waves-light" data-dismiss="modal" id="btnSuscripGiroSi">Si</button>
					</div>
					<div class="col-md-6">
						<button class="btn btn-blue waves-effect waves-light" id="btnSuscripGiroNo">No</button>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<!-- END Modal Giro Sub-giro -->


<!-- Modal Carga Masiva -->
<div class="modal" id="modalCargaMasiva" tabindex="-1" role="dialog" aria-labelledby="modalCargaMasivaLabel" aria-hidden="true">
	<div class="modal-dialog modal-lg modal-dialog-centered" role="document">
		<div class="modal-content">
			<div class="modal-header green">
				<h5 class="modal-title text-black-50" id="modalCargaMasivaLabel">
					<i class="fa fa-building"></i> Carga masiva de Ubicaciones
				</h5>
			</div>
			<!--Body-->
			<div class="modal-body">

				<div class="row">
					<div class="col-12">
						<div class="row">
							<div class="col-6"></div>
							<div class="col-4 pt-2">Descargar Archivo</div>
							<div class="col-2">
								<a id="btnDescargaXLS_CM" href="${urlDoc}" class="btn-floating btn-sm teal pt-2 pl-1 text-dark">XLS</a>
							</div>
						</div>
					</div>
				</div>
				<hr />
				<div class="row">
					<div class="col-12">
						<span>Archivo permitido Excel</span>
						<div class="md-form">
							<div class="file-field">
								<div class="btn btn-blue btn-rounded btn-sm float-left">
									<span>
										<i class="fas fa-upload mr-2" aria-hidden="true"></i>Seleccionar Archivo
									</span>
									<input id="docAgenSusc" type="file" data-file_types="xls|xlsx">
								</div>
								<input id="infDocSuc" class="form-control" type="text" placeholder="Archivo" disabled>
							</div>
						</div>
					</div>
				</div>

			</div>

			<!--Footer-->
			<div class="modal-footer justify-content-center">
				<div class="row">
					<div class="col-md-6">
						<button class="btn btn-pink waves-effect waves-light" id="btnCargMasiAcepta">Aceptar</button>
					</div>
					<div class="col-md-6">
						<button class="btn btn-blue waves-effect waves-light" data-dismiss="modal" id="btnCargMasiCancel">Cancelar</button>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<!-- END Modal carga masiva -->


<!-- Modal Bloqueo Agentes -->
<div class="modal" id="modalBloqueoAgente" tabindex="-1" role="dialog" aria-labelledby="modalBloqueoAgenteLabel" aria-hidden="true">
	<div class="modal-dialog modal-lg modal-dialog-centered" role="document">
		<div class="modal-content">
			<div class="modal-header orange">
				<h5 class="modal-title text-black-50" id="modalBloqueoAgenteLabel">
					<i class="fas fa-exclamation-triangle"></i> Restricción para Agente
				</h5>
				<button type="button" class="close" data-dismiss="modal" aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
			</div>

			<!--Body-->
			<div class="modal-body">
				<div class="row">
					<div class="col-12 text-justify">
						La clave de agente seleccionada debe actualizar su expediente de Agente para poder emitir pólizas.   Por el momento, solo podrá generar cotizaciones.   Dudas o comentarios, favor de escribir a <a href="mailto:enterate@tokiomarine.com.mx">enterate@tokiomarine.com.mx</a>.
					</div>
				</div>
			</div>

			<!--Footer-->
			<div class="modal-footer justify-content-center">
				<div class="row">
					<div class="col-md-6">
						<button class="btn btn-pink waves-effect waves-light" data-dismiss="modal" id="btncpsusc">Entendido</button>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<!-- END Modal Bloqueo Agentes -->