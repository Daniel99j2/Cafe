package com.daniel99j.cafe.site;

public class SiteTitleElement extends ElementParser {
    public SiteTitleElement() {
        super("sitetitle");
    }

    @Override
    public String parse(String data) {
        return """
                <script type="text/javascript">
                    function openHomePage() {
                        window.location.href = "%base%"
                    }
                </script>
                <p style="color: orangered; cursor: pointer;" onclick='openHomePage()'>NOM NOM IN MAH TOM TOM</p>
                """;
    }
}
