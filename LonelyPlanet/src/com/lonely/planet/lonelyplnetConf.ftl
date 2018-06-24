<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <title>Lonely Planet</title>
    <link href="../static/all.css" media="screen" rel="stylesheet" type="text/css">
  </head>

  <body>
    <div id="container">
      <div id="header">
        <div id="logo"></div>
        <h1>Lonely Planet: ${destination.title}</h1>
      </div>

      <div id="wrapper">
        <div id="sidebar">
          <div class="block">
            <h3>Navigation</h3>
            <div class="content">
              <div class="inner">
              <ul class="navigation">
					<#list children as child>
					<li><a href="${child.nodeId}.html">${child.name}</a></li>
					</#list>
			   </ul>
              </div>
            </div>
          </div>
        </div>

        <div id="main">
          <div class="block">
            <div class="secondary-navigation">
              <ul>
					<#list ancestors as ancestor>
					<li class="first"><a href="${ancestor.nodeId}.html">${ancestor.name}</a></li>
					</#list>
					<li class="first"><a href="#">${destination.title}</a></li>
              </ul>
              <div class="clear"></div>
            </div>
            <div class="content">
              <div class="inner">
	              <h3 class="title">Introduction Overview</h3>
	              ${(destination.html('introductory/introduction/overview'))!""}
	              <h3 class="title">History Overview</h3>
	              ${(destination.html('history/history/overview'))!""}
	              <h3 class="title">Before you go</h3>
	              ${(destination.html('practical_information/health_and_safety/before_you_go/0'))!""}
	              <h3 class="title">While you're there</h3>
	              ${(destination.html('practical_information/health_and_safety/while_youre_there'))!""}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </body>
</html>
