package org.mox.spikes.rx;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.netty.protocol.http.client.HttpClient;
import io.reactivex.netty.protocol.http.client.HttpClientResponse;
import io.reactivex.netty.protocol.http.server.HttpServer;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import io.reactivex.netty.protocol.http.server.HttpServerResponse;
import io.reactivex.netty.protocol.http.server.RequestHandler;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.PublishSubject;

import java.nio.charset.Charset;

import static io.reactivex.netty.RxNetty.createHttpClient;
import static io.reactivex.netty.RxNetty.createHttpServer;
import static io.reactivex.netty.protocol.http.client.HttpClientRequest.createGet;
import static rx.subjects.PublishSubject.create;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class RxNettyExample {

    public static void main(String[] args) throws InterruptedException {

        final PublishSubject<String> publisher = create();

        final HttpServer<ByteBuf, ByteBuf> server = createHttpServer(8080,
                new RequestHandler<ByteBuf, ByteBuf>() {

                    @Override
                    public Observable<Void> handle(HttpServerRequest<ByteBuf> request,
                            HttpServerResponse<ByteBuf> response) {

                        System.out.println("Server => Request: " + request.getPath());
                        try {
                            if (request.getPath().equals("/error")) {
                                throw new RuntimeException("forced error");
                            }

                            publisher.onNext("input data");

                            response.setStatus(HttpResponseStatus.OK);
                            return response.writeStringAndFlush(
                                    "Path Requested =>: " + request.getPath() + "\n");
                        } catch (Throwable e) {
                            System.err.println(
                                    "Server => Error [" + request.getPath() + "] => " + e);
                            response.setStatus(HttpResponseStatus.BAD_REQUEST);
                            return response.writeStringAndFlush("Error 500: Bad Request\n");
                        }
                    }

                });

        server.start();

        final HttpClient<ByteBuf, ByteBuf> client = createHttpClient("localhost", 8080);

        client.submit(createGet("/")).flatMap(
                new Func1<HttpClientResponse<ByteBuf>, Observable<ByteBuf>>() {

                    @Override
                    public Observable<ByteBuf> call(HttpClientResponse<ByteBuf> clientResponse) {

                        return clientResponse.getContent();
                    }
                }).map(new Func1<ByteBuf, String>() {

            @Override
            public String call(ByteBuf byteBuf) {

                return "Client => " + byteBuf.toString(Charset.defaultCharset());
            }
        }).toBlocking().forEach(new Action1<String>() {

            @Override
            public void call(String s) {

                System.out.println(s);
            }
        });

        client.submit(createGet("/error")).flatMap(
                new Func1<HttpClientResponse<ByteBuf>, Observable<ByteBuf>>() {

                    @Override
                    public Observable<ByteBuf> call(
                            HttpClientResponse<ByteBuf> byteBufHttpClientResponse) {

                        return byteBufHttpClientResponse.getContent();
                    }
                }).map(new Func1<ByteBuf, String>() {

            @Override
            public String call(ByteBuf data) {

                return "Client => " + data.toString(Charset.defaultCharset());
            }
        }).toBlocking().forEach(new Action1<String>() {

            @Override
            public void call(String s) {

                System.out.println(s);
            }
        });

        client.submit(createGet("/data")).flatMap(
                new Func1<HttpClientResponse<ByteBuf>, Observable<ByteBuf>>() {

                    @Override
                    public Observable<ByteBuf> call(
                            HttpClientResponse<ByteBuf> byteBufHttpClientResponse) {

                        return byteBufHttpClientResponse.getContent();
                    }
                }).map(new Func1<ByteBuf, String>() {

            @Override
            public String call(ByteBuf o) {

                return "Client => " + o.toString(Charset.defaultCharset());
            }
        }).toBlocking().forEach(new Action1<String>() {

            @Override
            public void call(String s) {

                System.out.println(s);
            }
        });

        server.shutdown();
    }
}
