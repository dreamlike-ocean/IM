
##handler配置
AllDecoder ->  AuthenticateHandler,MessageHeaderHandler(理论平级但是AuthenticateHandler在前)
AuthenticateHandler验证之后会将自己从pipeline中移除
AuthenticateHandler和其余Request
## 协议格式

由header+body构成

#### header格式

##### 用户发给server：

 前四位（int）type

再四位（int）sequenceId  如果不是type不是请求则为-1

后四位（int）payLoadLength

##### server发给用户

 前四位（int）type

再四位（int）sequenceId 如果不是type不是请求则为-1

后四位（int）payLoadLength

最后为（int）senderId
