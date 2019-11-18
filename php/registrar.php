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
	
	if(isset($_POST["nick"]) && isset($_POST["pass"]) && isset($_POST["name"]) && isset($_POST["birth"]) && isset($_POST["origin"]) && isset($_POST["mail"]) && isset($_POST["telf"]) && isset($_POST["description"])){
		$USER_NICK = $_POST["nick"];
		$USER_PASS = $_POST["pass"];
		$USER_NAME = $_POST["name"];
		$USER_BIRTH = $_POST["birth"];
		$USER_ORIGIN = $_POST["origin"];
		$USER_MAIL = $_POST["mail"];
		$USER_TELF = $_POST["telf"];
		$USER_DESCRIPTION = $_POST["description"];
		//Creamos la conexión
		$conn = mysqli_connect($server, $userName, $pass,$bd);
		if (mysqli_connect_errno()) {
		    print_r(json_encode(array("error_code" => 404,
							  	  "error_message" => "Not found")));
		    exit();
		}

		//generamos la consulta
		$stmt = $conn->prepare("INSERT INTO USER (USER_NICK, USER_PASS ,USER_NAME ,USER_PRIVACY ,USER_BIRTH ,USER_COUNTRY ,USER_MAIL ,USER_TELF ,USER_DESCRIPTION, USER_RATING, USER_FECHAMOD) VALUES (?,?,?,0,?,?,?,?,?,0, SYSDATE())");
		$stmt->bind_param("ssssssss", $USER_NICK, $USER_PASS, $USER_NAME, $USER_BIRTH, $USER_ORIGIN, $USER_MAIL, $USER_TELF, $USER_DESCRIPTION);

		if ($stmt->execute()) {
		  	$stmt->fetch();
	  		$stmt->close();	 		
			$miArray[] = array("exito" => true);		  	 	
		}else{
	  		$stmt->close();
	  		$miArray[] = array("error_code" => 409,
						  	  "error_message" => "El usuario ya existe");
	  	}		
	}else{
		$miArray[] = array("error_code" => 300,
						  "error_message" => "Parametros incorrectos");
	}
	print_r(json_encode($miArray));	
?>