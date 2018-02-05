@ SSQR.exe server.png -json | ^
busybox tr -d " " | ^
busybox tr -d "\n" | ^
busybox xargs -I {} jq --argjson abc {} ".outbound.settings.servers[0]=$abc" 123.json | ^
busybox tee config.json
