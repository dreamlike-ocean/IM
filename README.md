
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

#### request的body格式
json结构必须存在requestType用于指示当前请求的目的

pullUnread -> 拉取未读信息，响应中json的message为此次结果

pullRecord -> 拉取与某人所有的记录 必须包括number类型的receiver字段响应中，json的message为此次结果

#### 其余的body格式
[id1,id2,id3,id4 {text}] 文本

[id1,id2,id3&filename fileBuffer] 文件
