* Endpoints example
  * http://localhost:8080/tickers/binance/unsubscribe/ethusdt
  * http://localhost:8080/tickers/binance/subscribe/ethusdt
* Logging of tickers is enabled on debug level
* Docker build and run command in main directory:
  * ```sudo docker build . -t tickers```
  * ```sudo docker run -p 8080:8080 -it tickers```
* socket is available on ws://localhost:8080/tickers but is limited only to subscriptions 