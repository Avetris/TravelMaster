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
				$ruta = "img/lugares/".str_replace("-", "_", $id_dispositivo).getdate()[0].".jpg";
				$fp = fopen($ruta, 'w');
				$ruta = "http://galan.ehu.eus/umateos002/WEB/TravelMaster/".$ruta;
				fwrite($fp, base64_decode($datos -> {"imagen_lugar"}));
				if(fclose($fp)){
					$stmt2 = $conn->prepare("INSERT INTO PLACE (PLACE_CREATOR_ID, PLACE_CATEGORY, PLACE_NAME, PLACE_DESCRIPTION, PLACE_IMAGE, PLACE_LATITUDE, PLACE_LONGITUDE, PLACE_RATING, PLACE_FECHAMOD) VALUES (?,?,?,?,?,?,?,?, SYSDATE())");
					$stmt2->bind_param("issssddi", $datos -> {"id_creador"}, $datos -> {"categoria_lugar"}, $datos -> {"nombre_lugar"}, $datos -> {"descripcion_lugar"}, $ruta, $datos -> {"latitud_lugar"}, $datos -> {"longitud_lugar"}, $datos -> {"valoracion_lugar"});
					if($stmt2->execute()){
						$stmt->close();			  			
						$arrayFila["id_lugar"] = $stmt2->insert_id;
						$arrayFila["id_creador"] = $datos -> {"id_creador"};
						$arrayFila["nombre_lugar"] = $datos -> {"nombre_lugar"};
						$arrayFila["descripcion_lugar"] = $datos -> {"descripcion_lugar"};
						$arrayFila["imagen_lugar"] = $ruta;
						$arrayFila["latitud_lugar"] = $datos -> {"latitud_lugar"};
						$arrayFila["longitud_lugar"] = $datos -> {"longitud_lugar"};
						$arrayFila["categoria_lugar"] = $datos -> {"categoria_lugar"};
						$arrayFila["valoracion_lugar"] = $datos -> {"valoracion_lugar"};
						$miArray[] = $arrayFila;
						$stmt2->fetch();
			  			$stmt2->close();
					}else{
			  			$error = $stmt2->error; 
			  			$stmt2->close();
			  			$miArray[] = array("error_code" => 401,
							  	  "error_message" => $error);
						print_r(json_encode($miArray));	
						die();
					}
				}else{
					$miArray[] = array("error_code" => 403,
							  	  "error_message" => "No se ha podido crear el archivo");
						print_r(json_encode($miArray));	
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