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

	if(isset($_POST["auth_token"]) && isset($_POST["id_dispositivo"]) && isset($_POST["datos"])){
		$auth_token = $_POST["auth_token"];
		$id_dispositivo = $_POST["id_dispositivo"];
		$datos = json_decode($_POST["datos"]);

		//Creamos la conexión
		$conn = mysqli_connect($server, $userName, $pass,$bd);
		if (mysqli_connect_errno()) {
		    print_r(json_encode(array("error_code" => 404,
							  	  "error_message" => "Not found")));
		    exit();
		}
		$stmt = $conn->prepare("SELECT COUNT(1) FROM SECURITY WHERE SEC_AUTH_TOKEN = ? AND SEC_DISPOSITIVO = ?");
		$stmt->bind_param("ss", $auth_token, $id_dispositivo);
		if ($stmt->execute()) {
			$stmt->bind_result($count);	
			if($stmt->fetch() && $count > 0){
				$stmt->close();
				$stmt2 = $conn->prepare("REPLACE INTO FRIENDSHIP (FRIEND_USER_ID, FRIEND_ID, FRIEND_STATE, FRIEND_FECHAMOD) VALUES(?,?,?,SYSDATE())");
				$stmt2->bind_param("iii", $datos -> {"id_usuario"}, $datos -> {"id_amigo"}, $datos -> {"estado_amigo"});
				if($stmt2->execute()){
					$stmt2->fetch();
		  			$stmt2->close();
		  			$arrayFila = [];
					$arrayFila["id_usuario"] = $datos -> {"id_usuario"};
					$arrayFila["id_amigo"] = $datos -> {"id_amigo"};
					$arrayFila["estado"] = $datos -> {"estado_amigo"};
					$miArray[] = $arrayFila;
				}else{
		  			$error = $stmt2->error; 
		  			$stmt2->close();
		  			$miArray[] = array("error_code" => 401,
						  	  "error_message" => $error);
					print_r(json_encode($miArray));	
					die();
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
	print_r(json_encode($miArray));	
?>