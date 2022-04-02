****************
Interactive Demo
****************

Enter your SQL code and beautify it. Result can be saved as SQL, HTML or RTF.
Parser Errors will be highlighted. Please report failing valid SQL statements.


Uses a patched :downloads:`JSQLParser Snapshot <0.1.11/jsqlparser-4.3-SNAPSHOT.jar>`.


.. raw:: html

    <iframe id="vboxFrame" src="_static/vbox.html" height="480" width="100%"></iframe></div>
    <script>
      let url    = "_static/vbox.html";
      let params = "?"+window.location.search.substr(1);
      let iframeUrl = url+params;
      vboxFrame.src = iframeUrl;
    </script>
