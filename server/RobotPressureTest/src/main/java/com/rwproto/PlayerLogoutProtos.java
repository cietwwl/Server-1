// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: PlayerLogout.proto

package com.rwproto;

public final class PlayerLogoutProtos {
  private PlayerLogoutProtos() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  public interface PlayerLogoutRequestOrBuilder
      extends com.google.protobuf.MessageOrBuilder {
  }
  /**
   * Protobuf type {@code PlayerLogoutService.PlayerLogoutRequest}
   */
  public static final class PlayerLogoutRequest extends
      com.google.protobuf.GeneratedMessage
      implements PlayerLogoutRequestOrBuilder {
    // Use PlayerLogoutRequest.newBuilder() to construct.
    private PlayerLogoutRequest(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
      super(builder);
      this.unknownFields = builder.getUnknownFields();
    }
    private PlayerLogoutRequest(boolean noInit) { this.unknownFields = com.google.protobuf.UnknownFieldSet.getDefaultInstance(); }

    private static final PlayerLogoutRequest defaultInstance;
    public static PlayerLogoutRequest getDefaultInstance() {
      return defaultInstance;
    }

    public PlayerLogoutRequest getDefaultInstanceForType() {
      return defaultInstance;
    }

    private final com.google.protobuf.UnknownFieldSet unknownFields;
    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
      return this.unknownFields;
    }
    private PlayerLogoutRequest(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      initFields();
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e.getMessage()).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.rwproto.PlayerLogoutProtos.internal_static_PlayerLogoutService_PlayerLogoutRequest_descriptor;
    }

    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.rwproto.PlayerLogoutProtos.internal_static_PlayerLogoutService_PlayerLogoutRequest_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.rwproto.PlayerLogoutProtos.PlayerLogoutRequest.class, com.rwproto.PlayerLogoutProtos.PlayerLogoutRequest.Builder.class);
    }

    public static com.google.protobuf.Parser<PlayerLogoutRequest> PARSER =
        new com.google.protobuf.AbstractParser<PlayerLogoutRequest>() {
      public PlayerLogoutRequest parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new PlayerLogoutRequest(input, extensionRegistry);
      }
    };

    @java.lang.Override
    public com.google.protobuf.Parser<PlayerLogoutRequest> getParserForType() {
      return PARSER;
    }

    private void initFields() {
    }
    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized != -1) return isInitialized == 1;

      memoizedIsInitialized = 1;
      return true;
    }

    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      getSerializedSize();
      getUnknownFields().writeTo(output);
    }

    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;

      size = 0;
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }

    private static final long serialVersionUID = 0L;
    @java.lang.Override
    protected java.lang.Object writeReplace()
        throws java.io.ObjectStreamException {
      return super.writeReplace();
    }

    public static com.rwproto.PlayerLogoutProtos.PlayerLogoutRequest parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.rwproto.PlayerLogoutProtos.PlayerLogoutRequest parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.rwproto.PlayerLogoutProtos.PlayerLogoutRequest parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.rwproto.PlayerLogoutProtos.PlayerLogoutRequest parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.rwproto.PlayerLogoutProtos.PlayerLogoutRequest parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static com.rwproto.PlayerLogoutProtos.PlayerLogoutRequest parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }
    public static com.rwproto.PlayerLogoutProtos.PlayerLogoutRequest parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input);
    }
    public static com.rwproto.PlayerLogoutProtos.PlayerLogoutRequest parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input, extensionRegistry);
    }
    public static com.rwproto.PlayerLogoutProtos.PlayerLogoutRequest parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static com.rwproto.PlayerLogoutProtos.PlayerLogoutRequest parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }

    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(com.rwproto.PlayerLogoutProtos.PlayerLogoutRequest prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessage.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code PlayerLogoutService.PlayerLogoutRequest}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder>
       implements com.rwproto.PlayerLogoutProtos.PlayerLogoutRequestOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return com.rwproto.PlayerLogoutProtos.internal_static_PlayerLogoutService_PlayerLogoutRequest_descriptor;
      }

      protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return com.rwproto.PlayerLogoutProtos.internal_static_PlayerLogoutService_PlayerLogoutRequest_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                com.rwproto.PlayerLogoutProtos.PlayerLogoutRequest.class, com.rwproto.PlayerLogoutProtos.PlayerLogoutRequest.Builder.class);
      }

      // Construct using com.rwproto.PlayerLogoutProtos.PlayerLogoutRequest.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessage.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessage.alwaysUseFieldBuilders) {
        }
      }
      private static Builder create() {
        return new Builder();
      }

      public Builder clear() {
        super.clear();
        return this;
      }

      public Builder clone() {
        return create().mergeFrom(buildPartial());
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return com.rwproto.PlayerLogoutProtos.internal_static_PlayerLogoutService_PlayerLogoutRequest_descriptor;
      }

      public com.rwproto.PlayerLogoutProtos.PlayerLogoutRequest getDefaultInstanceForType() {
        return com.rwproto.PlayerLogoutProtos.PlayerLogoutRequest.getDefaultInstance();
      }

      public com.rwproto.PlayerLogoutProtos.PlayerLogoutRequest build() {
        com.rwproto.PlayerLogoutProtos.PlayerLogoutRequest result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public com.rwproto.PlayerLogoutProtos.PlayerLogoutRequest buildPartial() {
        com.rwproto.PlayerLogoutProtos.PlayerLogoutRequest result = new com.rwproto.PlayerLogoutProtos.PlayerLogoutRequest(this);
        onBuilt();
        return result;
      }

      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof com.rwproto.PlayerLogoutProtos.PlayerLogoutRequest) {
          return mergeFrom((com.rwproto.PlayerLogoutProtos.PlayerLogoutRequest)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(com.rwproto.PlayerLogoutProtos.PlayerLogoutRequest other) {
        if (other == com.rwproto.PlayerLogoutProtos.PlayerLogoutRequest.getDefaultInstance()) return this;
        this.mergeUnknownFields(other.getUnknownFields());
        return this;
      }

      public final boolean isInitialized() {
        return true;
      }

      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        com.rwproto.PlayerLogoutProtos.PlayerLogoutRequest parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (com.rwproto.PlayerLogoutProtos.PlayerLogoutRequest) e.getUnfinishedMessage();
          throw e;
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }

      // @@protoc_insertion_point(builder_scope:PlayerLogoutService.PlayerLogoutRequest)
    }

    static {
      defaultInstance = new PlayerLogoutRequest(true);
      defaultInstance.initFields();
    }

    // @@protoc_insertion_point(class_scope:PlayerLogoutService.PlayerLogoutRequest)
  }

  public interface PlayerLogoutResponseOrBuilder
      extends com.google.protobuf.MessageOrBuilder {
  }
  /**
   * Protobuf type {@code PlayerLogoutService.PlayerLogoutResponse}
   */
  public static final class PlayerLogoutResponse extends
      com.google.protobuf.GeneratedMessage
      implements PlayerLogoutResponseOrBuilder {
    // Use PlayerLogoutResponse.newBuilder() to construct.
    private PlayerLogoutResponse(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
      super(builder);
      this.unknownFields = builder.getUnknownFields();
    }
    private PlayerLogoutResponse(boolean noInit) { this.unknownFields = com.google.protobuf.UnknownFieldSet.getDefaultInstance(); }

    private static final PlayerLogoutResponse defaultInstance;
    public static PlayerLogoutResponse getDefaultInstance() {
      return defaultInstance;
    }

    public PlayerLogoutResponse getDefaultInstanceForType() {
      return defaultInstance;
    }

    private final com.google.protobuf.UnknownFieldSet unknownFields;
    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
      return this.unknownFields;
    }
    private PlayerLogoutResponse(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      initFields();
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e.getMessage()).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.rwproto.PlayerLogoutProtos.internal_static_PlayerLogoutService_PlayerLogoutResponse_descriptor;
    }

    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.rwproto.PlayerLogoutProtos.internal_static_PlayerLogoutService_PlayerLogoutResponse_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.rwproto.PlayerLogoutProtos.PlayerLogoutResponse.class, com.rwproto.PlayerLogoutProtos.PlayerLogoutResponse.Builder.class);
    }

    public static com.google.protobuf.Parser<PlayerLogoutResponse> PARSER =
        new com.google.protobuf.AbstractParser<PlayerLogoutResponse>() {
      public PlayerLogoutResponse parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new PlayerLogoutResponse(input, extensionRegistry);
      }
    };

    @java.lang.Override
    public com.google.protobuf.Parser<PlayerLogoutResponse> getParserForType() {
      return PARSER;
    }

    private void initFields() {
    }
    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized != -1) return isInitialized == 1;

      memoizedIsInitialized = 1;
      return true;
    }

    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      getSerializedSize();
      getUnknownFields().writeTo(output);
    }

    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;

      size = 0;
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }

    private static final long serialVersionUID = 0L;
    @java.lang.Override
    protected java.lang.Object writeReplace()
        throws java.io.ObjectStreamException {
      return super.writeReplace();
    }

    public static com.rwproto.PlayerLogoutProtos.PlayerLogoutResponse parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.rwproto.PlayerLogoutProtos.PlayerLogoutResponse parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.rwproto.PlayerLogoutProtos.PlayerLogoutResponse parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.rwproto.PlayerLogoutProtos.PlayerLogoutResponse parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.rwproto.PlayerLogoutProtos.PlayerLogoutResponse parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static com.rwproto.PlayerLogoutProtos.PlayerLogoutResponse parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }
    public static com.rwproto.PlayerLogoutProtos.PlayerLogoutResponse parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input);
    }
    public static com.rwproto.PlayerLogoutProtos.PlayerLogoutResponse parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input, extensionRegistry);
    }
    public static com.rwproto.PlayerLogoutProtos.PlayerLogoutResponse parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static com.rwproto.PlayerLogoutProtos.PlayerLogoutResponse parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }

    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(com.rwproto.PlayerLogoutProtos.PlayerLogoutResponse prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessage.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code PlayerLogoutService.PlayerLogoutResponse}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder>
       implements com.rwproto.PlayerLogoutProtos.PlayerLogoutResponseOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return com.rwproto.PlayerLogoutProtos.internal_static_PlayerLogoutService_PlayerLogoutResponse_descriptor;
      }

      protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return com.rwproto.PlayerLogoutProtos.internal_static_PlayerLogoutService_PlayerLogoutResponse_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                com.rwproto.PlayerLogoutProtos.PlayerLogoutResponse.class, com.rwproto.PlayerLogoutProtos.PlayerLogoutResponse.Builder.class);
      }

      // Construct using com.rwproto.PlayerLogoutProtos.PlayerLogoutResponse.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessage.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessage.alwaysUseFieldBuilders) {
        }
      }
      private static Builder create() {
        return new Builder();
      }

      public Builder clear() {
        super.clear();
        return this;
      }

      public Builder clone() {
        return create().mergeFrom(buildPartial());
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return com.rwproto.PlayerLogoutProtos.internal_static_PlayerLogoutService_PlayerLogoutResponse_descriptor;
      }

      public com.rwproto.PlayerLogoutProtos.PlayerLogoutResponse getDefaultInstanceForType() {
        return com.rwproto.PlayerLogoutProtos.PlayerLogoutResponse.getDefaultInstance();
      }

      public com.rwproto.PlayerLogoutProtos.PlayerLogoutResponse build() {
        com.rwproto.PlayerLogoutProtos.PlayerLogoutResponse result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public com.rwproto.PlayerLogoutProtos.PlayerLogoutResponse buildPartial() {
        com.rwproto.PlayerLogoutProtos.PlayerLogoutResponse result = new com.rwproto.PlayerLogoutProtos.PlayerLogoutResponse(this);
        onBuilt();
        return result;
      }

      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof com.rwproto.PlayerLogoutProtos.PlayerLogoutResponse) {
          return mergeFrom((com.rwproto.PlayerLogoutProtos.PlayerLogoutResponse)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(com.rwproto.PlayerLogoutProtos.PlayerLogoutResponse other) {
        if (other == com.rwproto.PlayerLogoutProtos.PlayerLogoutResponse.getDefaultInstance()) return this;
        this.mergeUnknownFields(other.getUnknownFields());
        return this;
      }

      public final boolean isInitialized() {
        return true;
      }

      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        com.rwproto.PlayerLogoutProtos.PlayerLogoutResponse parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (com.rwproto.PlayerLogoutProtos.PlayerLogoutResponse) e.getUnfinishedMessage();
          throw e;
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }

      // @@protoc_insertion_point(builder_scope:PlayerLogoutService.PlayerLogoutResponse)
    }

    static {
      defaultInstance = new PlayerLogoutResponse(true);
      defaultInstance.initFields();
    }

    // @@protoc_insertion_point(class_scope:PlayerLogoutService.PlayerLogoutResponse)
  }

  private static com.google.protobuf.Descriptors.Descriptor
    internal_static_PlayerLogoutService_PlayerLogoutRequest_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_PlayerLogoutService_PlayerLogoutRequest_fieldAccessorTable;
  private static com.google.protobuf.Descriptors.Descriptor
    internal_static_PlayerLogoutService_PlayerLogoutResponse_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_PlayerLogoutService_PlayerLogoutResponse_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\022PlayerLogout.proto\022\023PlayerLogoutServic" +
      "e\"\025\n\023PlayerLogoutRequest\"\026\n\024PlayerLogout" +
      "ResponseB!\n\013com.rwprotoB\022PlayerLogoutPro" +
      "tos"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
      new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
        public com.google.protobuf.ExtensionRegistry assignDescriptors(
            com.google.protobuf.Descriptors.FileDescriptor root) {
          descriptor = root;
          internal_static_PlayerLogoutService_PlayerLogoutRequest_descriptor =
            getDescriptor().getMessageTypes().get(0);
          internal_static_PlayerLogoutService_PlayerLogoutRequest_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessage.FieldAccessorTable(
              internal_static_PlayerLogoutService_PlayerLogoutRequest_descriptor,
              new java.lang.String[] { });
          internal_static_PlayerLogoutService_PlayerLogoutResponse_descriptor =
            getDescriptor().getMessageTypes().get(1);
          internal_static_PlayerLogoutService_PlayerLogoutResponse_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessage.FieldAccessorTable(
              internal_static_PlayerLogoutService_PlayerLogoutResponse_descriptor,
              new java.lang.String[] { });
          return null;
        }
      };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
  }

  // @@protoc_insertion_point(outer_class_scope)
}
