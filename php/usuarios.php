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
	
		$sql = "SELECT USER_ID, USER_NICK, USER_NAME, USER_PRIVACY, USER_BIRTH, USER_COUNTRY, USER_IMAGE, USER_MAIL, USER_TELF, USER_DESCRIPTION, USER_RATING FROM USER";

		if(count($date) != 0){
			$sql = $sql." WHERE USER_FECHAMOD >= ?";		
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
				$stmt->bind_result($user_id, $user_nick, $user_name, $user_privacy, $user_birth, $user_country, $user_image, $user_mail, $user_telf, $user_description, $user_rating);	
				$miArray = [];
				while($stmt->fetch()){
					$arrayFila = [];
					$arrayFila["id_usuario"] = $user_id;
					$arrayFila["nick_usuario"] = $user_nick;
					$arrayFila["nombre_usuario"] = $user_name;
					$arrayFila["privado_usuario"] = $user_privacy;
					$arrayFila["cumpleanos_usuario"] = $user_birth;
					$arrayFila["imagen_usuario"] = $user_image;
					$arrayFila["pais_usuario"] = $user_country;
					$arrayFila["email_usuario"] = $user_mail;
					$arrayFila["telefono_usuario"] = $user_telf;
					$arrayFila["descripcion_usuario"] = $user_description;
					$arrayFila["valoracion_usuario"] = $user_rating;					
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