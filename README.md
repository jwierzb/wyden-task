* Endpoints example
  * http://localhost:8080/tickers/binance/unsubscribe/ethusdt
  * http://localhost:8080/tickers/binance/subscribe/ethusdt
* Logging of tickers is enabled on debug level
* Docker build and run command in main directory:
  * ```sudo docker build . -t tickers```
  * ```sudo docker run -p 8080:8080 -it tickers```
* socket is available on ws://localhost:8080/tickers but is limited only to subscriptions

Known issues
* I have focused on adding new features instead of testing existing ones, thus small number of test (probably mistake)
* Forget how to declare dockerfile as an executable so we could just run dockerfile run -it image ticker1,ticker2, so hardcoded argument with default ticker
* backpressure is implemented on websocket client level, probably not what was requested (I assume we could want to avoid deserializing too much of data as it's quite expensive)
* Did not touch margin
* Reconnections not working correctly, probably due to error in OkHttp configuration - after wifi disconnection OkHttp do not call any related callbacks
