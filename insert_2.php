<?php 

    error_reporting(E_ALL); 
    ini_set('display_errors',1); 

    include('dbcon_2.php');


    $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");


    if( (($_SERVER['REQUEST_METHOD'] == 'POST') && isset($_POST['submit'])) || $android )
    {

        // 안드로이드 코드의 postParameters 변수에 적어준 이름을 가지고 값을 전달 받습니다.

        $id=$_POST['id'];
        $pass=$_POST['pass'];
        $name=$_POST['name'];
        $Num=$_POST['Num'];
        $weight=$_POST['weight'];

        if(empty($id)){
            $errMSG = "ID을 입력하세요.";
        }
        else if(empty($pass)){
            $errMSG = "passwd를 입력하세요.";
	}
	else if(empty($name)){
            $errMSG = "name를 입력하세요.";
	}
	else if(empty($Num)){
            $errMSG = "Num를 입력하세요.";
	}
	else if(empty($weight)){
            $errMSG = "weight를 입력하세요.";
        }

        if(!isset($errMSG)) // 이름과 나라 모두 입력이 되었다면 
        {
            try{
                // SQL문을 실행하여 데이터를 MySQL 서버의 person 테이블에 저장합니다. 
                $stmt = $con->prepare('INSERT INTO person(id, pass, name, Num, weight) VALUES(:id, :pass, :name, :Num, :weight)');
                $stmt->bindParam(':id', $id);
                $stmt->bindParam(':pass', $pass);
                $stmt->bindParam(':name', $name);
                $stmt->bindParam(':Num', $Num);
                $stmt->bindParam(':weight', $weight);

                if($stmt->execute())
                {
                    $successMSG = "새로운 사용자를 추가했습니다.";
                }
                else
                {
                    $errMSG = "사용자 추가 에러";
                }

            } catch(PDOException $e) {
                die("Database error: " . $e->getMessage()); 
            }
        }

    }

?>


<?php 
    if (isset($errMSG)) echo $errMSG;
    if (isset($successMSG)) echo $successMSG;

	$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");
   
    if( !$android )
    {
?>
    <html>
       <body>

            <form action="<?php $_PHP_SELF ?>" method="POST">
                ID: <input type = "text" name = "id" />
                Passwd: <input type = "text" name = "pass" />
                Name: <input type = "text" name = "name" />
                Num: <input type = "text" name = "Num" />
                weight: <input type = "text" name = "weight" />
                <input type = "submit" name = "submit" />
            </form>
       
       </body>
    </html>

<?php 
    }
?>
