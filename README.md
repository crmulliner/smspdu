smspdu
======

send raw PDU SMS from your computer using a HTC Android phone (download precompiled APK: [smspdu.apk](http://github.com/crmulliner/smspdu/raw/master/smspdu.apk))

This tool is for research purposes since it is hard to send raw PDU SMS from cell phones these days. The tool uses the hidden method 'sendRawPdu' that is present in HTC devices.

smspdu listens on port tcp/2323 on your phone, it expects one line of HEX encoded SMS in PDU submit format. The PDU is directly passed to sendRawPdu. After the message is send the connection is closed.
