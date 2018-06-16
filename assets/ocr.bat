@ curl -k --form "file=@123.png" -H "apikey: webocr5" https://api.ocr.space/parse/image | ^
jq .ParsedResults["0"].ParsedText
