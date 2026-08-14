package guru.qa.niffler.test.grpc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.grpc.NifflerUserdataServiceGrpc;
import guru.qa.niffler.utils.GrpcConsoleInterceptor;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;

public class BaseUserdataGrpcTest {

    protected static final Config CFG = Config.getInstance();

    protected static final Channel channel = ManagedChannelBuilder
            .forAddress(CFG.userdataGrpcAddress(), CFG.userdataGrpcPort())
            .intercept(new GrpcConsoleInterceptor())
            .usePlaintext()
            .build();

    protected static final NifflerUserdataServiceGrpc.NifflerUserdataServiceBlockingStub blockingStub
            = NifflerUserdataServiceGrpc.newBlockingStub(channel);


}
