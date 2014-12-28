displayed = {};
listed = {};
neighbors = {};

function submitSuccessCallback(data, status, xhr) {
	console.log(data);
	if(data.length == 1 && data[0]['_id'] == -1) {
		alert(data['properties']['error']);
	} else {
		$("#typeInput").val("");
		$("#nameInput").val("");
		$("#resultsSelect").empty();
		listed = {};
		if( Object.prototype.toString.call( data ) !== '[object Array]' ) {
			data = [data];
		}
		for(var idx in data) {
			var vertex = data[idx];
			listed[vertex['_id']] = vertex['properties'];
			var option = $('<option></option>').attr("value", vertex['_id']).text(vertex['properties']['type'] + ':' + vertex['properties']['name'] + ':' + vertex['_id']);
			$("#resultsSelect").append(option);
		}
	}
}

function submitSuccessEdgeCallback(data, status, xhr) {
	console.log(data);
	if(data['status'] == 'success') {
		console.log(data['success']);
	} else {
		alert(data['error']);
	}
}

function getNeighborsSuccessCallback(data, status, xhr) {
	console.log(data);
	if(data.length == 1 && data[0]['_id'] == -1) {
		alert(data['error']);
	} else {
		$("#neighborSelect").empty();
		neighbors = {};
		if( Object.prototype.toString.call( data ) !== '[object Array]' ) {
			data = [data];
		}
		for(var idx in data) {
			var vertex = data[idx];
			neighbors[vertex['_id']] = vertex['properties'];
			var option = $('<option></option>').attr("value", vertex['_id']).text(vertex['properties']['type'] + ':' + vertex['properties']['name'] + ':' + vertex['_id']);
			$("#neighborSelect").append(option);
		}
	}
}

function deleteVertex() {
	var apiKey = $("#apiKeyInput").val();
	var vertex = $("#vertexInput").val();
	var data = {"apiKey": apiKey, "vertex": vertex};

	console.log(data);
	$.ajax({
		headers:{'Accept': 'application/json', 'Content-Type':'application/json'},
		'type': 'POST',
		'url': '/IntelligenceGraph/api/utility/delete_vertex/',
		'data': JSON.stringify(data),
		'dataType': 'json',
		'success': submitSuccessEdgeCallback,
		'error': submitErrorCallback });
}

function getNeighbors() {
	var apiKey = $("#apiKeyInput").val();
	var vertex = $("#vertexInput").val();
	var data = {"apiKey": apiKey, "vertex": vertex};

	console.log(data);
	$.ajax({
		headers:{'Accept': 'application/json', 'Content-Type':'application/json'},
		'type': 'POST',
		'url': '/IntelligenceGraph/api/utility/get_neighbors/',
		'data': JSON.stringify(data),
		'dataType': 'json',
		'success': getNeighborsSuccessCallback,
		'error': submitErrorCallback });
}

function createEdge() {
	var apiKey = $("#apiKeyInput").val();
	var idA = $("#idAInput").val();
	var idB = $("#idBInput").val();
	var data = {"apiKey": apiKey, "vertexA":idA, "vertexB": idB};

	console.log(data);
	$.ajax({
		headers:{'Accept': 'application/json', 'Content-Type':'application/json'},
		'type': 'POST',
		'url': '/IntelligenceGraph/api/utility/create_edge/',
		'data': JSON.stringify(data),
		'dataType': 'json',
		'success': submitSuccessEdgeCallback,
		'error': submitErrorCallback });
}

function updateDisplay() {
	$('#displaySelect').empty();
	var keys = Object.keys(displayed);
	for(idx in keys) {
		var key = keys[idx];
		var vertex = displayed[key];
		var option = $('<option></option>').attr('value', key).text(vertex['type'] + ':' + vertex['name'] + ':' + key);
		$('#displaySelect').append(option);
	}
}

function displayVertices() {
	console.log($("#resultsSelect").val());
	var values = $("#resultsSelect").val();
	for(var idx in values) {
		var id = values[idx];
		displayed[id] = listed[id];
	}
	updateDisplay();
	console.log(displayed);
}

function submitErrorCallback(xhr, status, error) {
	console.log(xhr);
	console.log(status);
	console.log(error);
}

function createVertex() {
	var apiKey = $("#apiKeyInput").val();
	var type = $("#typeInput").val();
	var name = $("#nameInput").val();

	var data = {"apiKey": apiKey, "type":type, "name": name};

	console.log(data);
	$.ajax({
		headers:{'Accept': 'application/json', 'Content-Type':'application/json'},
		'type': 'POST',
		'url': '/IntelligenceGraph/api/utility/create_vertex/',
		'data': JSON.stringify(data),
		'dataType': 'json',
		'success': submitSuccessCallback,
		'error': submitErrorCallback });
}

function updateVertex() {
	var apiKey = $("#apiKeyInput").val();
	var vertex = $("#vertexInput").val();
	var key = $("#keyInput").val();
	var value = $("#valueInput").val();

	var data = {"apiKey": apiKey, "vertex": vertex };
	data[key] = value;

	console.log(data);
	$.ajax({
		headers:{'Accept': 'application/json', 'Content-Type':'application/json'},
		'type': 'POST',
		'url': '/IntelligenceGraph/api/utility/update_vertex/',
		'data': JSON.stringify(data),
		'dataType': 'json',
		'success': submitSuccessCallback,
		'error': submitErrorCallback });
}

function searchVertices() {
	var apiKey = $("#apiKeyInput").val();
	var type = $("#typeInput").val();
	var name = $("#nameInput").val();

	var data = {"apiKey": apiKey, "type":type, "name": name};

	console.log(data);
	$.ajax({
		headers:{'Accept': 'application/json', 'Content-Type':'application/json'},
		'type': 'POST',
		'url': '/IntelligenceGraph/api/utility/search_vertices/',
		'data': JSON.stringify(data),
		'dataType': 'json',
		'success': submitSuccessCallback,
		'error': submitErrorCallback });
}

function displayedOnChange() {
	var values = $("#displaySelect").val();
	console.log(values);
	if(values.length > 0) {
		var id = values[0];
		$("#vertexInput").val(id);
	}
}