package guru.qa.niffler.service;

import com.google.protobuf.Empty;
import guru.qa.niffler.data.CurrencyValues;
import guru.qa.niffler.grpc.AcceptInvitationRequest;
import guru.qa.niffler.grpc.AllUsersPageRequest;
import guru.qa.niffler.grpc.AllUsersRequest;
import guru.qa.niffler.grpc.CurrentUserRequest;
import guru.qa.niffler.grpc.DeclineInvitationRequest;
import guru.qa.niffler.grpc.Direction;
import guru.qa.niffler.grpc.FriendsPageRequest;
import guru.qa.niffler.grpc.FriendsRequest;
import guru.qa.niffler.grpc.NifflerUserdataServiceGrpc;
import guru.qa.niffler.grpc.PageInfo;
import guru.qa.niffler.grpc.RegisterPushTokenRequest;
import guru.qa.niffler.grpc.RemoveFriendRequest;
import guru.qa.niffler.grpc.SendInvitationRequest;
import guru.qa.niffler.grpc.UpdateUserRequest;
import guru.qa.niffler.grpc.User;
import guru.qa.niffler.grpc.UserResponse;
import guru.qa.niffler.grpc.UsersPageResponse;
import guru.qa.niffler.model.FriendshipStatus;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.model.UserJsonBulk;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.UUID;

@GrpcService
public class UserdataGrpcService extends NifflerUserdataServiceGrpc.NifflerUserdataServiceImplBase {

    private static final Logger LOG = LoggerFactory.getLogger(UserdataGrpcService.class);

    private final UserService userService;
    private final PushTokenService pushTokenService;

    @Autowired
    public UserdataGrpcService(UserService userService, PushTokenService pushTokenService) {
        this.userService = userService;
        this.pushTokenService = pushTokenService;
    }

    @Override
    public void getCurrentUser(CurrentUserRequest request, StreamObserver<UserResponse> responseObserver) {
        try {
            String username = request.getUsername();
            LOG.info("Getting current user: {}", username);

            UserJson user = userService.getCurrentUser(username);
            UserResponse response = UserResponse.newBuilder()
                    .setUser(mapToUser(user))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            LOG.error("Error getting current user", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void updateUser(UpdateUserRequest request, StreamObserver<UserResponse> responseObserver) {
        try {
            User grpcUser = request.getUser();
            LOG.info("Updating user: {}", grpcUser.getUsername());

            UserJson userJson = mapFromGrpcUser(grpcUser);
            UserJson updatedUser = userService.update(userJson);

            UserResponse response = UserResponse.newBuilder()
                    .setUser(mapToUser(updatedUser))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            LOG.error("Error updating user", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void getAllUsers(AllUsersRequest request, StreamObserver<UsersPageResponse> responseObserver) {
        try {
            String username = request.getUsername();
            String searchQuery = request.hasSearchQuery() ? request.getSearchQuery() : null;
            LOG.info("Getting all users for: {}, search: {}", username, searchQuery);

            List<UserJsonBulk> users = userService.allUsers(username, searchQuery);
            List<User> grpcUsers = users.stream()
                    .map(this::mapToUserBulk)
                    .toList();

            UsersPageResponse response = UsersPageResponse.newBuilder()
                    .setTotalElements(grpcUsers.size())
                    .setTotalPages(1)
                    .setFirst(true)
                    .setLast(true)
                    .setSize(grpcUsers.size())
                    .setNumber(0)
                    .setEmpty(grpcUsers.isEmpty())
                    .addAllEdges(grpcUsers)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            LOG.error("Error getting all users", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void getAllUsersV2(AllUsersPageRequest request, StreamObserver<UsersPageResponse> responseObserver) {
        try {
            String username = request.getUsername();
            Pageable pageable = buildPageable(request.getPageInfo());
            String searchQuery = request.hasSearchQuery() ? request.getSearchQuery() : null;
            LOG.info("Getting all users V2 for: {}, page: {}, size: {}, search: {}",
                    username, request.getPageInfo().getPage(), request.getPageInfo().getSize(), searchQuery);

            Page<UserJsonBulk> users = userService.allUsers(username, pageable, searchQuery);
            UsersPageResponse response = buildPageResponse(users);

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            LOG.error("Error getting all users V2", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void getFriends(FriendsRequest request, StreamObserver<UsersPageResponse> responseObserver) {
        try {
            String username = request.getUsername();
            String searchQuery = request.hasSearchQuery() ? request.getSearchQuery() : null;
            LOG.info("Getting friends for: {}, search: {}", username, searchQuery);

            List<UserJsonBulk> friends = userService.friends(username, searchQuery);
            List<User> grpcUsers = friends.stream()
                    .map(this::mapToUserBulk)
                    .toList();

            UsersPageResponse response = UsersPageResponse.newBuilder()
                    .setTotalElements(grpcUsers.size())
                    .setTotalPages(1)
                    .setFirst(true)
                    .setLast(true)
                    .setSize(grpcUsers.size())
                    .setNumber(0)
                    .setEmpty(grpcUsers.isEmpty())
                    .addAllEdges(grpcUsers)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            LOG.error("Error getting friends", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void getFriendsV2(FriendsPageRequest request, StreamObserver<UsersPageResponse> responseObserver) {
        try {
            String username = request.getUsername();
            Pageable pageable = buildPageable(request.getPageInfo());
            String searchQuery = request.hasSearchQuery() ? request.getSearchQuery() : null;
            LOG.info("Getting friends V2 for: {}, page: {}, size: {}, search: {}",
                    username, request.getPageInfo().getPage(), request.getPageInfo().getSize(), searchQuery);

            Page<UserJsonBulk> friends = userService.friends(username, pageable, searchQuery);
            UsersPageResponse response = buildPageResponse(friends);

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            LOG.error("Error getting friends V2", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void removeFriend(RemoveFriendRequest request, StreamObserver<Empty> responseObserver) {
        try {
            String username = request.getUsername();
            String friendToBeRemoved = request.getFriendToBeRemoved();
            LOG.info("Removing friend: {} -> {}", username, friendToBeRemoved);

            userService.removeFriend(username, friendToBeRemoved);

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();

        } catch (Exception e) {
            LOG.error("Error removing friend", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void sendInvitation(SendInvitationRequest request, StreamObserver<UserResponse> responseObserver) {
        try {
            String username = request.getUsername();
            String friendToBeRequested = request.getFriendToBeRequested();
            LOG.info("Sending invitation: {} -> {}", username, friendToBeRequested);

            UserJson user = userService.createFriendshipRequest(username, friendToBeRequested);
            UserResponse response = UserResponse.newBuilder()
                    .setUser(mapToUser(user))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            LOG.error("Error sending invitation", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void acceptInvitation(AcceptInvitationRequest request, StreamObserver<UserResponse> responseObserver) {
        try {
            String username = request.getUsername();
            String friendToBeAdded = request.getFriendToBeAdded();
            LOG.info("Accepting invitation: {} -> {}", username, friendToBeAdded);

            UserJson user = userService.acceptFriendshipRequest(username, friendToBeAdded);
            UserResponse response = UserResponse.newBuilder()
                    .setUser(mapToUser(user))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            LOG.error("Error accepting invitation", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void declineInvitation(DeclineInvitationRequest request, StreamObserver<UserResponse> responseObserver) {
        try {
            String username = request.getUsername();
            String invitationToBeDeclined = request.getInvitationToBeDeclined();
            LOG.info("Declining invitation: {} -> {}", username, invitationToBeDeclined);

            UserJson user = userService.declineFriendshipRequest(username, invitationToBeDeclined);
            UserResponse response = UserResponse.newBuilder()
                    .setUser(mapToUser(user))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            LOG.error("Error declining invitation", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void registerPushToken(RegisterPushTokenRequest request, StreamObserver<Empty> responseObserver) {
        try {
            String username = request.getUsername();
            String token = request.getToken();
            String userAgent = request.getUserAgent();
            LOG.info("Registering push token for: {}", username);

            pushTokenService.upsert(username, token, userAgent);

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();

        } catch (Exception e) {
            LOG.error("Error registering push token", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    private User mapToUser(UserJson user) {
        User.Builder builder = User.newBuilder()
                .setId(user.id() != null ? user.id().toString() : "")
                .setUsername(user.username())
                .setCurrency(mapCurrency(user.currency()));

        // Используем null проверки вместо hasXxx()
        if (user.firstname() != null && !user.firstname().isEmpty()) {
            builder.setFirstname(user.firstname());
        }
        if (user.surname() != null && !user.surname().isEmpty()) {
            builder.setSurname(user.surname());
        }
        if (user.fullname() != null && !user.fullname().isEmpty()) {
            builder.setFullname(user.fullname());
        }
        if (user.photo() != null && !user.photo().isEmpty()) {
            builder.setPhoto(user.photo());
        }
        if (user.photoSmall() != null && !user.photoSmall().isEmpty()) {
            builder.setPhotoSmall(user.photoSmall());
        }
        if (user.friendshipStatus() != null) {
            builder.setFriendshipStatus(mapFriendshipStatus(user.friendshipStatus()));
        }

        return builder.build();
    }

    private User mapToUserBulk(UserJsonBulk user) {
        User.Builder builder = User.newBuilder()
                .setId(user.id() != null ? user.id().toString() : "")
                .setUsername(user.username())
                .setCurrency(mapCurrency(user.currency()));

        if (user.fullname() != null && !user.fullname().isEmpty()) {
            builder.setFullname(user.fullname());
        }
        if (user.photoSmall() != null && !user.photoSmall().isEmpty()) {
            builder.setPhotoSmall(user.photoSmall());
        }
        if (user.friendshipStatus() != null) {
            builder.setFriendshipStatus(mapFriendshipStatus(user.friendshipStatus()));
        }

        return builder.build();
    }

    private UserJson mapFromGrpcUser(User grpcUser) {
        grpcUser.getFirstname();
        grpcUser.getSurname();
        grpcUser.getFullname();
        grpcUser.getPhoto();
        grpcUser.getPhotoSmall();
        return new UserJson(
                grpcUser.getId().isEmpty() ? null : UUID.fromString(grpcUser.getId()),
                grpcUser.getUsername(),
                !grpcUser.getFirstname().isEmpty() ? grpcUser.getFirstname() : null,
                !grpcUser.getSurname().isEmpty() ? grpcUser.getSurname() : null,
                !grpcUser.getFullname().isEmpty() ? grpcUser.getFullname() : null,
                mapCurrency(grpcUser.getCurrency()),
                !grpcUser.getPhoto().isEmpty() ? grpcUser.getPhoto() : null,
                !grpcUser.getPhotoSmall().isEmpty() ? grpcUser.getPhotoSmall() : null,
                grpcUser.getFriendshipStatus() != guru.qa.niffler.grpc.FriendshipStatus.VOID
                        ? mapFriendshipStatus(grpcUser.getFriendshipStatus()) : null
        );
    }

    private UsersPageResponse buildPageResponse(Page<UserJsonBulk> page) {
        List<User> grpcUsers = page.getContent().stream()
                .map(this::mapToUserBulk)
                .toList();

        return UsersPageResponse.newBuilder()
                .setTotalElements((int) page.getTotalElements())
                .setTotalPages(page.getTotalPages())
                .setFirst(page.isFirst())
                .setLast(page.isLast())
                .setSize(page.getSize())
                .setNumber(page.getNumber())
                .setEmpty(page.isEmpty())
                .addAllEdges(grpcUsers)
                .build();
    }

    private Pageable buildPageable(PageInfo pageInfo) {
        int page = pageInfo.getPage();
        int size = pageInfo.getSize();

        if (pageInfo.getSortCount() == 0) {
            return PageRequest.of(page, size);
        }

        Sort sort = Sort.unsorted();
        for (guru.qa.niffler.grpc.Sort sortInfo : pageInfo.getSortList()) {
            Sort.Direction direction = sortInfo.getDirection() == Direction.ASC
                    ? Sort.Direction.ASC
                    : Sort.Direction.DESC;
            sort = sort.and(Sort.by(direction, sortInfo.getProperty()));
        }

        return PageRequest.of(page, size, sort);
    }

    private guru.qa.niffler.grpc.CurrencyValues mapCurrency(CurrencyValues currency) {
        if (currency == null) return guru.qa.niffler.grpc.CurrencyValues.RUB;

        return switch (currency) {
            case RUB -> guru.qa.niffler.grpc.CurrencyValues.RUB;
            case USD -> guru.qa.niffler.grpc.CurrencyValues.USD;
            case EUR -> guru.qa.niffler.grpc.CurrencyValues.EUR;
            case KZT -> guru.qa.niffler.grpc.CurrencyValues.KZT;
        };
    }

    private CurrencyValues mapCurrency(guru.qa.niffler.grpc.CurrencyValues currency) {
        return switch (currency) {
            case RUB -> CurrencyValues.RUB;
            case USD -> CurrencyValues.USD;
            case EUR -> CurrencyValues.EUR;
            case KZT -> CurrencyValues.KZT;
            case UNRECOGNIZED -> CurrencyValues.RUB;
        };
    }

    private guru.qa.niffler.grpc.FriendshipStatus mapFriendshipStatus(FriendshipStatus status) {
        if (status == null) return guru.qa.niffler.grpc.FriendshipStatus.VOID;

        return switch (status) {
            case INVITE_SENT -> guru.qa.niffler.grpc.FriendshipStatus.INVITE_SENT;
            case INVITE_RECEIVED -> guru.qa.niffler.grpc.FriendshipStatus.INVITE_RECEIVED;
            case FRIEND -> guru.qa.niffler.grpc.FriendshipStatus.FRIEND;
            default -> guru.qa.niffler.grpc.FriendshipStatus.VOID;
        };
    }

    private FriendshipStatus mapFriendshipStatus(guru.qa.niffler.grpc.FriendshipStatus status) {
        return switch (status) {
            case INVITE_SENT -> FriendshipStatus.INVITE_SENT;
            case INVITE_RECEIVED -> FriendshipStatus.INVITE_RECEIVED;
            case FRIEND -> FriendshipStatus.FRIEND;
            case VOID -> null;
            case UNRECOGNIZED -> null;
        };
    }
}