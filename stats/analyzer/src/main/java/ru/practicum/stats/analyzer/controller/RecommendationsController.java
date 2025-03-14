package ru.practicum.stats.analyzer.controller;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.grpc.stats.analyzer.RecommendationsControllerGrpc;
import ru.practicum.grpc.stats.recommendation.InteractionsCountRequestProto;
import ru.practicum.grpc.stats.recommendation.RecommendedEventProto;
import ru.practicum.grpc.stats.recommendation.SimilarEventsRequestProto;
import ru.practicum.grpc.stats.recommendation.UserPredictionsRequestProto;
import ru.practicum.stats.analyzer.service.RecommendationsService;

import java.util.stream.Stream;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class RecommendationsController extends RecommendationsControllerGrpc.RecommendationsControllerImplBase {
    private final RecommendationsService recommendationsService;

    @Override
    public void getRecommendationsForUser(UserPredictionsRequestProto request, StreamObserver<RecommendedEventProto> responseObserver) {
        log.info("getRecommendationsForUser invoked with request: {}", request);
        Stream<RecommendedEventProto> recommendedEvents = recommendationsService
                .getRecommendationsForUser(request.getUserId(), request.getMaxResults());
        try {
            recommendedEvents.forEach(responseObserver::onNext);
            responseObserver.onCompleted();
            log.info("getRecommendationsForUser request is successfully handled: {}", request);
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(Status.fromThrowable(e)));
        }
    }

    @Override
    public void getSimilarEvents(SimilarEventsRequestProto request, StreamObserver<RecommendedEventProto> responseObserver) {
        log.info("getSimilarEventsWithoutInteractions invoked with request: {}", request);
        Stream<RecommendedEventProto> recommendedEvents = recommendationsService
                .getSimilarEvents(request.getEventId(), request.getUserId(), request.getMaxResults());
        try {
            recommendedEvents.forEach(responseObserver::onNext);
            responseObserver.onCompleted();
            log.info("getSimilarEventsWithoutInteractions request is successfully handled: {}", request);
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(Status.fromThrowable(e)));
        }
    }

    @Override
    public void getInteractionsCount(InteractionsCountRequestProto request, StreamObserver<RecommendedEventProto> responseObserver) {
        log.info("getInteractionsCount invoked with request: {}", request);
        Stream<RecommendedEventProto> recommendedEvents = recommendationsService.getInteractionsCount(request.getEventIdList());
        try {
            recommendedEvents.forEach(responseObserver::onNext);
            responseObserver.onCompleted();
            log.info("getInteractionsCount request is successfully handled: {}", request);
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(Status.fromThrowable(e)));
        }
    }
}