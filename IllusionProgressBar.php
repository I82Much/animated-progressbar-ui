<?php  header("Content-type: application/x-java-jnlp-file");
echo "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";?>
<jnlp spec="1.0+" codebase="https://github.com/I82Much/animated-progressbar-ui/raw/DaneelVersion/" href="IllusionProgressBar.php">
    <information>
        <title>IllusionProgressBar Demo</title>
        <vendor>Daneel Reventelov</vendor>
    </information>
    <resources>
        <!-- Application Resources -->
        <j2se version="1.7+"/>
		<jar href="IllusionProgressBar.jar"/>
    </resources>
    <application-desc main-class="Loader"/>
</jnlp>

