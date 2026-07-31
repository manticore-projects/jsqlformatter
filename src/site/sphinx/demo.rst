****************
Interactive Demo
****************

Paste a statement, pick your options, and see the result immediately. The demo
runs the current development build against
|JSQLPARSER_SNAPSHOT_VERSION_LINK|.

.. raw:: html

	<iframe id="jsqlformatterFrame"
	        src="../_static/jsqlformatter_demo.html"
	        height="560"
	        width="100%"
	        style="border:0;border-radius:8px;"
	        title="JSQLFormatter interactive demo"></iframe>
	<script>
	  (function () {
	    var frame = document.getElementById("jsqlformatterFrame");
	    var query = window.location.search;
	    if (frame && query.length > 1) {
	      frame.src = "../_static/jsqlformatter_demo.html?" + query.substring(1);
	    }
	  })();
	</script>

.. note::

   The demo is also reachable standalone at
   `jsqlformatter.manticore-projects.com <http://jsqlformatter.manticore-projects.com>`_.