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
	
		$sql = "SELECT REVIEW_USER_ID, REVIEW_PLACE_ID, REVIEW_TITLE, REVIEW_DESCRIPTION, REVIEW_RATING FROM REVIEW";
		if(count($date) != 0){
			$sql = $sql." WHERE REVIEW_FECHAMOD >= ?";		
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
				$stmt->bind_result($user_id, $place_id, $review_title, $review_description, $review_rating);	
				$miArray = [];
				while($stmt->fetch()){
					$arrayFila = [];
					$arrayFila["id_usuario"] = $user_id;
					$arrayFila["id_lugar"] = $place_id;
					$arrayFila["titulo_review"] = $review_title;
					$arrayFila["descripcion_review"] = $review_description;
					$arrayFila["valoracion_review"] = $review_rating;
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