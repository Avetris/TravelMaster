<?php 
	ini_set('display_errors', 'On');
	ini_set('display_errors', 1);
	error_reporting(E_ALL);
	mb_internal_encoding('UTF-8');
	mb_http_output('UTF-8');
	$server = "galan.ehu.eus";
	$userName = "Xumateos002";
	$pass = "yic3cp8T";
	$bd = "Xumateos002_TravelMaster";
	$miArray = [];
	if(isset($_POST["nick"]) && isset($_POST["password"]) && isset($_POST["id_dispositivo"])){
		$nick = $_POST["nick"];
		$password = $_POST["password"];
		$id_dispositivo = $_POST["id_dispositivo"];
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

		//generamos la consulta
		$stmt = $conn->prepare("SELECT USER_ID, USER_NICK, USER_NAME, USER_PRIVACY, USER_BIRTH, USER_IMAGE, USER_COUNTRY, USER_MAIL, USER_TELF, USER_DESCRIPTION, USER_RATING FROM USER WHERE USER_NICK=? AND USER_PASS = ?");
		$stmt->bind_param('ss', $nick, $password);
		$stmt->execute();
		$stmt->bind_result($user_id, $user_nick, $user_name, $user_privacy, $user_birth, $user_image, $user_country, $user_mail, $user_telf, $user_description, $user_rating);	

		if($stmt->fetch()){
				$arrayFila = [];
				$arrayFila["id_usuario"] = $user_id;
				$arrayFila["nick_usuario"] = $user_nick;
				$arrayFila["nombre_usuario"] = $user_name;
				$arrayFila["privado_usuario"] = $user_privacy;
				$arrayFila["cumpleanos_usuario"] = $user_birth;
				$arrayFila["pais_usuario"] = $user_country;
				$arrayFila["email_usuario"] = $user_mail;
				$arrayFila["telefono_usuario"] = $user_telf;
				$arrayFila["descripcion_usuario"] = $user_description;
				$arrayFila["valoracion_usuario"] = $user_rating;
				$arrayFila["imagen_usuario"] = $user_image;
		  		$stmt->close();
		 		$auth_token = "";
		  		$string = "abcdefghijklmnopqrstuvwxyz0123456789"; 
			  	for($i=0;$i<25;$i++){ 
				    $pos = rand(0,35); 
				    $auth_token .= $string{$pos}; 
				} 
				$arrayFila["auth_token"] = $auth_token;
				$stmt2 = $conn->prepare("REPLACE INTO SECURITY (SEC_AUTH_TOKEN, SEC_USER_ID, SEC_DISPOSITIVO) VALUES (?, ?, ?)");
				$stmt2->bind_param("sis", $auth_token, $arrayFila["id_usuario"], $id_dispositivo);
				$stmt2->execute();
				$stmt2->close();
				$miArray[] = $arrayFila;
		}else{
			$miArray[] = array("error_code" => 401,
							  	  "error_message" => "Autentificacion incorrecta");
		}
		
	}else{
		$miArray[] = array("error_code" => 300,
						  "error_message" => "Parametros incorrectos");
	}
	print_r(json_encode($miArray, JSON_UNESCAPED_UNICODE));	
?>