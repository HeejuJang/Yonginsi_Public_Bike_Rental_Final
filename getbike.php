<?php 

    
error_reporting(E_ALL); 
    
ini_set('display_errors',1); 

    
include('dbcon_2.php');
        

    
$stmt = $con->prepare('select * from bike');
    
$stmt->execute();

    if ($stmt->rowCount() > 0)
    {
        
$data = array(); 

        
while($row=$stmt->fetch(PDO::FETCH_ASSOC))
        {
            
extract($row);
    
            
array_push($data, 
                
array('bid'=>$bid,
                
'QRcode'=>$QRcode,
                
'status'=>$status
            ));
        }

        
header('Content-Type: application/json; charset=utf8');
        
$json = json_encode(array("webnautes"=>$data), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
        echo $json;
    }

?>
