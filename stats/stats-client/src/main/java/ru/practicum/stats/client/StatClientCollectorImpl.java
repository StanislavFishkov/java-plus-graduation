package ru.practicum.stats.client;

import com.google.protobuf.Timestamp;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import ru.practicum.grpc.stats.action.ActionTypeProto;
import ru.practicum.grpc.stats.action.UserActionProto;
import ru.practicum.grpc.stats.collector.UserActionControllerGrpc;

import java.time.Instant;

@Slf4j
@Component
public class StatClientCollectorImpl implements StatClientCollector {
    @GrpcClient("collector")
    private UserActionControllerGrpc.UserActionControllerBlockingStub collectorClient;

    @Override
    public void collectUserAction(long userId, long eventId, ActionTypeProto actionTypeProto, Instant timestamp) {
        UserActionProto request = UserActionProto.newBuilder()
                .setUserId(userId)
                .setEventId(eventId)
                .setActionType(actionTypeProto)
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(timestamp.getEpochSecond())
                        .setNanos(timestamp.getNano())
                        .build()
                )
                .build();

        log.debug("Stat client called for collectUserAction with request: {}", request);
        collectorClient.collectUserAction(request);
    }
}