package ru.practicum.stats.collector.contoller;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.grpc.stats.action.UserActionProto;
import ru.practicum.grpc.stats.collector.UserActionControllerGrpc;
import ru.practicum.stats.collector.service.EventService;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class UserActionController extends UserActionControllerGrpc.UserActionControllerImplBase {
    private final EventService eventService;

    @Override
    public void collectUserAction(UserActionProto request, StreamObserver<Empty> responseObserver) {
        log.info("collectUserAction invoked with request: {}", request);
        try {
            eventService.collectUserAction(request);
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
            log.info("collectUserAction request is successfully handled: {}", request);
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(Status.fromThrowable(e)));
        }
    }
}