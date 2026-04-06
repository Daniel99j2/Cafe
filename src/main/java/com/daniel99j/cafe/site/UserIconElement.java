package com.daniel99j.cafe.site;

public class UserIconElement extends ElementParser {
    public UserIconElement() {
        super("usericon");
    }

    @Override
    public String parse(String data) {
        return """
                <script type="text/javascript">
                    function openProfile() {
                        window.location.href = "%base%/account"
                    }
                </script>
                <div style="display: block; position:fixed; top:10px; right:20px;">
                    <p onclick='openProfile()' style="cursor: pointer;">👤</p>
                </div>
                """;
    }
}
