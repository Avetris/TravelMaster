<?php 
	ini_set('display_errors', 'On');
	ini_set('display_errors', 1);
	error_reporting(1);
	mb_internal_encoding('UTF-8');
	mb_http_output('UTF-8');
	$server = "galan.ehu.eus";
	$userName = "Xumateos002";
	$pass = "yic3cp8T";
	$bd = "Xumateos002_TravelMaster";	
	$miArray = [];

	if(isset($_POST["auth_token"]) && isset($_POST["id_dispositivo"])){
		$auth_token = $_POST["auth_token"];
		$id_dispositivo = $_POST["id_dispositivo"];
		if(isset($_POST["fecha_desde"])){
			$date = $_POST["fecha_desde"];
		}else{
			$date = [];
		}

		//Creamos la conexión
		$conn = mysqli_connect($server, $userName, $pass,$bd);
		if (mysqli_connect_errno()) {
		    print_r(json_encode(array("error_code" => 404,
							  	  "error_message" => "Not found")));
		    exit();
		}

	    /* change character set to utf8 */
	    if (!$conn->set_charset("utf8")) {
	        $conn->error;
	    } else {
	        $conn->character_set_name();
	    }
	
		$sql = "SELECT PLACE_CREATOR_ID, PLACE_ID, PLACE_CATEGORY, PLACE_NAME, PLACE_DESCRIPTION, PLACE_IMAGE, PLACE_LATITUDE, PLACE_LONGITUDE, PLACE_RATING FROM PLACE";
		if(count($date) != 0){
			$sql = $sql." WHERE PLACE_FECHAMOD >= ?";		
		}

		$stmt = $conn->prepare("SELECT COUNT(1) FROM SECURITY WHERE SEC_AUTH_TOKEN = ? AND SEC_DISPOSITIVO = ?");
		$stmt->bind_param("ss", $auth_token, $id_dispositivo);
		if ($stmt->execute()) {
			$stmt->bind_result($count);	
			if($stmt->fetch() && $count > 0){
				$stmt->close();
				$stmt = $conn->prepare($sql);
				if(count($date) > 0){
					$stmt->bind_param("s", $date);
				}
				$stmt->execute();
				$stmt->bind_result($id_creator, $place_id, $place_category, $place_name, $place_description, $place_image, $place_latitude, $place_longitude, $place_rating);	
				$miArray = [];
				while($stmt->fetch()){
					$arrayFila = [];
					$arrayFila["id_lugar"] = $place_id;
					$arrayFila["id_creador"] = $id_creator;
					$arrayFila["nombre_lugar"] = $place_name;
					$arrayFila["descripcion_lugar"] = $place_description;
					$arrayFila["imagen_lugar"] = $place_image;
					$arrayFila["latitud_lugar"] = $place_latitude;
					$arrayFila["longitud_lugar"] = $place_longitude;
					$arrayFila["categoria_lugar"] = $place_category;
					$arrayFila["valoracion_lugar"] = $place_rating;
					$miArray[] = $arrayFila;
    			}
			}else{
				$stmt->close();
		  		$miArray[] = array("error_code" => 401,
							  	  "error_message" => "El usuario debe estar logueado");
			}			
		}
	}else{
		$miArray[] = array("error_code" => 300,
						  "error_message" => "Parametros incorrectos");
	}
	print_r(json_encode($miArray, JSON_UNESCAPED_UNICODE));	
?>