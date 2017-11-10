<?php
error_reporting(0);
function connect_db() {	
    try{
        $pdo=new PDO("mysql:host=localhost;dbname=newmania","root","rafa8808");
    }
    catch(PDOException $e){
        die($e->getCode());
    }

    return $pdo;
}



if(!$_GET['id'] || !$_GET['token']){
	die();
}

$player_id = $_GET['id'];
$pass1 = $_GET['token'];

function update_sso_ticket($id, $pass, $ticket) {
    $pdo = connect_db();
    $stm = $pdo->prepare('UPDATE users SET auth_ticket = :auth_ticket WHERE (id = :id AND password = :pass) LIMIT 1');
    $stm->bindValue(':id', $id, PDO::PARAM_INT);
	$stm->bindValue(':pass', $pass, PDO::PARAM_STR);
    $stm->bindValue(':auth_ticket', $ticket, PDO::PARAM_STR);
    $stm->execute();
}

function load_player_by_id($id, $pass) {
    $pdo = connect_db();

    $stm = $pdo->prepare('SELECT * FROM users WHERE (id = :id AND password = :pass) LIMIT 1');
    $stm->bindValue(':id', $id, PDO::PARAM_INT);
	$stm->bindValue(':pass', $pass, PDO::PARAM_STR);
    $stm->execute();

    return $stm->fetch(PDO::FETCH_ASSOC);
}
$player = load_player_by_id($player_id, $pass1);

$ticket  = 'ON'. $player['id'] .'MANIA'. rand(1000,9999) .'SSO'. rand(1000,9999) . '-2017';
update_sso_ticket($player_id, $pass1, $ticket);

$pdo = connect_db();
$stm = $pdo->prepare('SELECT * FROM cms_client');
$stm->execute();
$client = $stm->fetch(PDO::FETCH_ASSOC);
?>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
    <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->

    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=0, minimal-ui">
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />

    <title>BETA</title>

    <meta name="description" content="Mania Hotel: Crie seu Habbo, Construa seu quarto, Converse e Faça Novos Amigos! Novo Site, Novos Mobis, Novas Roupas e muito mais. Junte-se ao melhor Habbo Pirata do Brasil!"/>
    <meta name="keywords" content="habblet, habblethotel, ihabi, habbopop, habbopoop, hebbo, habblive, habb, lella, lellahotel,lella hotel, habbinfo, habbinfo hotel, habblive, habblive hotel, habbolatino, habbletlatino, habblet, habblethotel, crazzy, habb, habbhotel , furnis , mobs, client, cliente, client hotel, clienthotel, atualizado, catalogo, mania, maniahotel, mania hotel, "/>
    <meta name="apple-itunes-app" content="app-id=472937654"><meta name="google-play-app" content="app-id=com.cloudmosa.puffinFree">

    <meta property="og:image" content="https://www.mania.gg/assets/images/img_facebook.png">
    <link rel="shortcut icon" href="https://www.mania.gg/assets/images/favicon.gif">
    <link rel="apple-touch-icon" sizes="144x144" href="https://www.mania.gg/assets/images/favicon.gif">
    <link rel="apple-touch-icon" sizes="114x114" href="https://www.mania.gg/assets/images/favicon.gif">
    <link rel="apple-touch-icon" sizes="72x72" href="https://www.mania.gg/assets/images/favicon.gif">
    <link rel="apple-touch-icon" sizes="57x57" href="https://www.mania.gg/assets/images/favicon.gif">

    <meta property="og:title" content="MANIA HOTEL - BETA" />
    <meta property="og:description" content="Mania Hotel: Crie seu Habbo, Construa seu quarto, Converse e Faça Novos Amigos! Novo Site, Novos Mobis, Novas Roupas e muito mais. Junte-se ao melhor Habbo Pirata do Brasil!" />
    <meta property="og:url" content="https://www.mania.gg/" />

    <link rel="stylesheet" href="https://www.mania.gg//assets/css/client.css" type="text/css">

    <script type="text/javascript" src="https://www.mania.gg/assets/bower_components/jquery/js/jquery.min.js"></script>
    <script type="text/javascript" src="https://www.mania.gg/assets/bower_components/jquery-ui/js/jquery-ui.min.js"></script>
    <script type="text/javascript" src="https://www.mania.gg/assets/js/swfobject.js"></script>
    <script type="text/javascript" src="https://www.mania.gg/assets/js/habboapi.js"></script>
    <script type="text/javascript">
        function requestFlashPermission() {
            var iframe = document.createElement('iframe');
            iframe.src = 'https://get.adobe.com/flashplayer';
            iframe.sandbox = '';
            document.body.appendChild(iframe);
            document.body.removeChild(iframe);
        }


        var isNewEdge = (navigator.userAgent.match(/Edge\/(\d+)/) || [])[1] > 14;
        var isNewSafari = (navigator.userAgent.match(/OS X (\d+)/) || [])[1] > 9;
        var isNewChrome = (navigator.userAgent.match(/Chrom(e|ium)\/(\d+)/) || [])[2] > 56
            && !/Mobile/i.test(navigator.userAgent);
        var canRequestPermission = isNewEdge || isNewSafari || isNewChrome;

        if (!swfobject.hasFlashPlayerVersion('10') && canRequestPermission) {
            // in case, when flash is not available, try to prompt user to enable it
            requestFlashPermission();
            // Chrome requires user's click in order to allow iframe embeding
            $(window).one('click', requestFlashPermission);
        }
    </script>

    <style>
        .menu_hotel {
            cursor: pointer;
            background: #111010;
            width: auto;
            height: 40px;
            padding: 20px;
            border: 2px solid #494949;
            margin: 0px;
            font-size: 14px;
            float: left;
            font-weight: 500;
            color: white !important;
            display: block;
            white-space: nowrap;
            position: relative;
            margin-top: -50px;
            font-family: Verdana,Arial,Helvetica,sans-serif;
            margin-left: 10px;
            border-radius: 5px;
            -moz-opacity: 0.5;
            opacity: 0.5;
            filter: alpha(opacity=50);
        }
    </style>

    <script type="text/javascript">
        function toggleFullScreen() {
            if ((document.fullScreenElement && document.fullScreenElement !== null) ||
                (!document.mozFullScreen && !document.webkitIsFullScreen)) {
                if (document.documentElement.requestFullScreen) {
                    document.documentElement.requestFullScreen();
                } else if (document.documentElement.mozRequestFullScreen) {
                    document.documentElement.mozRequestFullScreen();
                } else if (document.documentElement.webkitRequestFullScreen) {
                    document.documentElement.webkitRequestFullScreen(Element.ALLOW_KEYBOARD_INPUT);
                }
            } else {
                if (document.cancelFullScreen) {
                    document.cancelFullScreen();
                } else if (document.mozCancelFullScreen) {
                    document.mozCancelFullScreen();
                } else if (document.webkitCancelFullScreen) {
                    document.webkitCancelFullScreen();
                }
            }
        }
        function resizeClient(){
            var theClient = document.getElementById('client');
            var theWidth = theClient.clientWidth;
            theClient.style.width = "10px";
            theClient.style.width = theWidth + "px";
        }
    </script>

    <link rel="stylesheet" type="text/css" href="https://fonts.googleapis.com/css?family=Ubuntu:regular,bold&subset=Latin">
    <style type="text/css">a:link,a:visited{text-decoration:none;color:#FFF;}</style>

    <script type="text/javascript">
        var flashvars = {
            "client.allow.cross.domain" : "1",
            "client.notify.cross.domain" : "0",
            "connection.info.host" : "<?php echo $client['client_ip']; ?>",
            "connection.info.port" : "<?php echo $client['client_port']; ?>",
            "site.url" : "<?php echo $client['client_url_prefix']; ?>",
            "url.prefix" : "<?php echo $client['client_url_prefix']; ?>",
            "client.reload.url" : "<?php echo $client['client_url_prefix']; ?>/hotel",
            "client.fatal.error.url" : "<?php echo $client['client_url_prefix']; ?>/hotel",
            "logout.url" : "<?php echo $client['client_url_prefix']; ?>/logout",
            "logout.disconnect.url" : "<?php echo $client['client_url_prefix']; ?>/client",
            "client.connection.failed.url" : "<?php echo $client['client_url_prefix']; ?>/client",
            "external.variables.txt" : "<?php echo $client['variables']; ?>",
            <?php if (!empty($client['override_variables'])): ?>
            "external.override.variables.txt" : "<?php echo $client['override_variables']; ?>",
            <?php endif; ?>
            "external.texts.txt" : "<?php echo $client['flash_texts']; ?>",
            <?php if (!empty($client['override_flash_texts'])): ?>
            "external.override.texts.txt" : "<?php echo $client['override_flash_texts']; ?>",
            <?php endif; ?>
            "productdata.load.url" : "<?php echo $client['product_data']; ?>",
            "furnidata.load.url" : "<?php echo $client['furni_data']; ?>",
            "photo.upload.url" : "<?php echo str_replace('{0}', $player, $client['photo_upload']); ?>",
            "avatareditor.promohabbos": "https://www.habbo.com.br/api/public/lists/hotlooks",
            "use.sso.ticket" : "1",
            "sso.ticket" : "<?php echo $ticket; ?>",
            "processlog.enabled" : "0",
            "client.starting.revolving" : "Quando você menos esperar...terminaremos de carregar.../Carregando mensagem divertida! Por favor espere./Você quer batatas fritas para acompanhar?/Siga o pato amarelo./O tempo é apenas uma ilusão./Já chegamos?!/Eu gosto da sua camiseta./Olhe para um lado. Olhe para o outro. Pisque duas vezes. Pronto!/Não é você, sou eu./Shhh! Estou tentando pensar aqui./Carregando o universo de pixels.",
            "flash.client.url" : "<?php echo $client['flash_client']; ?>",
            "flash.client.origin" : "popup",
        };
    </script>

    <script type="text/javascript">
        var params = {
            "base" : "<?php echo $client['flash_client']; ?>",
            "allowScriptAccess" : "always",
            "menu" : "false",
            "wmode": "opaque"
        };

        swfobject.embedSWF('<?php echo $client['flash_client']; echo $client['client_swf'] ?>', 'client', '100%', '100%', '11.1.0', '//images.habbo.com/habboweb/63_1d5d8853040f30be0cc82355679bba7c/10449/web-gallery/flash/expressInstall.swf', flashvars, params, null, null);
    </script>
</head>

<body>
<div id="client"></div>
<div id="client-ui" >
    <div id="flash-wrapper">
        <div id="flash-container">
            <div id="content" style="width: 400px; margin: 20px auto 0 auto; display: none">
                <div class="cbb clearfix">
                    <h2 class="title">Atualize o Flash Player para a última versão.</h2>
                    <div class="box-content">
                        <p>You can install and download Adobe Flash Player here: <a href="http://get.adobe.com/flashplayer/">Install flash player</a>. More instructions for installation can be found here: <a href="http://www.adobe.com/products/flashplayer/productinfo/instructions/">More information</a></p>
                    </div>
                </div>
            </div>
            <noscript>
                <div style="width: 400px; margin: 20px auto 0 auto; text-align: center">
                    <p>If you are not automatically redirected, please <a href="/client/nojs">click here</a></p>
                </div>
            </noscript>
        </div>
    </div>

    <div id="content" class="client-content"></div>
</div>
</body>
</html>
