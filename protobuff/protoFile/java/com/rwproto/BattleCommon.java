// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: BattleCommon.proto

package com.rwproto;

public final class BattleCommon {
  private BattleCommon() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  /**
   * Protobuf enum {@code BattleCommon.ePlayerType}
   */
  public enum ePlayerType
      implements com.google.protobuf.ProtocolMessageEnum {
    /**
     * <code>MAINPLAYER = 0;</code>
     *
     * <pre>
     *主角
     * </pre>
     */
    MAINPLAYER(0, 0),
    /**
     * <code>HIRE = 1;</code>
     *
     * <pre>
     *佣兵
     * </pre>
     */
    HIRE(1, 1),
    /**
     * <code>MONSTER = 2;</code>
     *
     * <pre>
     *怪物
     * </pre>
     */
    MONSTER(2, 2),
    /**
     * <code>MONSTER_TINY_BOSS = 3;</code>
     *
     * <pre>
     *小boss
     * </pre>
     */
    MONSTER_TINY_BOSS(3, 3),
    /**
     * <code>MONSTER_LARGE_BOSS = 4;</code>
     *
     * <pre>
     *大boss
     * </pre>
     */
    MONSTER_LARGE_BOSS(4, 4),
    /**
     * <code>INVISIBLE_NPC = 5;</code>
     *
     * <pre>
     *不可见Ncp用来放技能
     * </pre>
     */
    INVISIBLE_NPC(5, 5),
    /**
     * <code>SUMMON_NORMAL = 6;</code>
     *
     * <pre>
     *普通召唤物
     * </pre>
     */
    SUMMON_NORMAL(6, 6),
    /**
     * <code>SUMMON_ANIMAL = 7;</code>
     *
     * <pre>
     *小型召唤物
     * </pre>
     */
    SUMMON_ANIMAL(7, 7),
    ;

    /**
     * <code>MAINPLAYER = 0;</code>
     *
     * <pre>
     *主角
     * </pre>
     */
    public static final int MAINPLAYER_VALUE = 0;
    /**
     * <code>HIRE = 1;</code>
     *
     * <pre>
     *佣兵
     * </pre>
     */
    public static final int HIRE_VALUE = 1;
    /**
     * <code>MONSTER = 2;</code>
     *
     * <pre>
     *怪物
     * </pre>
     */
    public static final int MONSTER_VALUE = 2;
    /**
     * <code>MONSTER_TINY_BOSS = 3;</code>
     *
     * <pre>
     *小boss
     * </pre>
     */
    public static final int MONSTER_TINY_BOSS_VALUE = 3;
    /**
     * <code>MONSTER_LARGE_BOSS = 4;</code>
     *
     * <pre>
     *大boss
     * </pre>
     */
    public static final int MONSTER_LARGE_BOSS_VALUE = 4;
    /**
     * <code>INVISIBLE_NPC = 5;</code>
     *
     * <pre>
     *不可见Ncp用来放技能
     * </pre>
     */
    public static final int INVISIBLE_NPC_VALUE = 5;
    /**
     * <code>SUMMON_NORMAL = 6;</code>
     *
     * <pre>
     *普通召唤物
     * </pre>
     */
    public static final int SUMMON_NORMAL_VALUE = 6;
    /**
     * <code>SUMMON_ANIMAL = 7;</code>
     *
     * <pre>
     *小型召唤物
     * </pre>
     */
    public static final int SUMMON_ANIMAL_VALUE = 7;


    public final int getNumber() { return value; }

    public static ePlayerType valueOf(int value) {
      switch (value) {
        case 0: return MAINPLAYER;
        case 1: return HIRE;
        case 2: return MONSTER;
        case 3: return MONSTER_TINY_BOSS;
        case 4: return MONSTER_LARGE_BOSS;
        case 5: return INVISIBLE_NPC;
        case 6: return SUMMON_NORMAL;
        case 7: return SUMMON_ANIMAL;
        default: return null;
      }
    }

    public static com.google.protobuf.Internal.EnumLiteMap<ePlayerType>
        internalGetValueMap() {
      return internalValueMap;
    }
    private static com.google.protobuf.Internal.EnumLiteMap<ePlayerType>
        internalValueMap =
          new com.google.protobuf.Internal.EnumLiteMap<ePlayerType>() {
            public ePlayerType findValueByNumber(int number) {
              return ePlayerType.valueOf(number);
            }
          };

    public final com.google.protobuf.Descriptors.EnumValueDescriptor
        getValueDescriptor() {
      return getDescriptor().getValues().get(index);
    }
    public final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptorForType() {
      return getDescriptor();
    }
    public static final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptor() {
      return com.rwproto.BattleCommon.getDescriptor().getEnumTypes().get(0);
    }

    private static final ePlayerType[] VALUES = values();

    public static ePlayerType valueOf(
        com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
      if (desc.getType() != getDescriptor()) {
        throw new java.lang.IllegalArgumentException(
          "EnumValueDescriptor is not for this type.");
      }
      return VALUES[desc.getIndex()];
    }

    private final int index;
    private final int value;

    private ePlayerType(int index, int value) {
      this.index = index;
      this.value = value;
    }

    // @@protoc_insertion_point(enum_scope:BattleCommon.ePlayerType)
  }

  /**
   * Protobuf enum {@code BattleCommon.ePlayerCamp}
   */
  public enum ePlayerCamp
      implements com.google.protobuf.ProtocolMessageEnum {
    /**
     * <code>Me = 0;</code>
     *
     * <pre>
     *我方
     * </pre>
     */
    Me(0, 0),
    /**
     * <code>Enemy = 1;</code>
     *
     * <pre>
     *敌方
     * </pre>
     */
    Enemy(1, 1),
    /**
     * <code>Other = 2;</code>
     *
     * <pre>
     *其他
     * </pre>
     */
    Other(2, 2),
    ;

    /**
     * <code>Me = 0;</code>
     *
     * <pre>
     *我方
     * </pre>
     */
    public static final int Me_VALUE = 0;
    /**
     * <code>Enemy = 1;</code>
     *
     * <pre>
     *敌方
     * </pre>
     */
    public static final int Enemy_VALUE = 1;
    /**
     * <code>Other = 2;</code>
     *
     * <pre>
     *其他
     * </pre>
     */
    public static final int Other_VALUE = 2;


    public final int getNumber() { return value; }

    public static ePlayerCamp valueOf(int value) {
      switch (value) {
        case 0: return Me;
        case 1: return Enemy;
        case 2: return Other;
        default: return null;
      }
    }

    public static com.google.protobuf.Internal.EnumLiteMap<ePlayerCamp>
        internalGetValueMap() {
      return internalValueMap;
    }
    private static com.google.protobuf.Internal.EnumLiteMap<ePlayerCamp>
        internalValueMap =
          new com.google.protobuf.Internal.EnumLiteMap<ePlayerCamp>() {
            public ePlayerCamp findValueByNumber(int number) {
              return ePlayerCamp.valueOf(number);
            }
          };

    public final com.google.protobuf.Descriptors.EnumValueDescriptor
        getValueDescriptor() {
      return getDescriptor().getValues().get(index);
    }
    public final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptorForType() {
      return getDescriptor();
    }
    public static final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptor() {
      return com.rwproto.BattleCommon.getDescriptor().getEnumTypes().get(1);
    }

    private static final ePlayerCamp[] VALUES = values();

    public static ePlayerCamp valueOf(
        com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
      if (desc.getType() != getDescriptor()) {
        throw new java.lang.IllegalArgumentException(
          "EnumValueDescriptor is not for this type.");
      }
      return VALUES[desc.getIndex()];
    }

    private final int index;
    private final int value;

    private ePlayerCamp(int index, int value) {
      this.index = index;
      this.value = value;
    }

    // @@protoc_insertion_point(enum_scope:BattleCommon.ePlayerCamp)
  }

  /**
   * Protobuf enum {@code BattleCommon.eBattlePositionType}
   *
   * <pre>
   *阵容信息-模块
   * </pre>
   */
  public enum eBattlePositionType
      implements com.google.protobuf.ProtocolMessageEnum {
    /**
     * <code>GroupSecretPos = 1;</code>
     *
     * <pre>
     *秘境
     * </pre>
     */
    GroupSecretPos(0, 1),
    /**
     * <code>PeakArenaPos = 2;</code>
     *
     * <pre>
     *巅峰竞技场
     * </pre>
     */
    PeakArenaPos(1, 2),
    /**
     * <code>ArenaPos = 3;</code>
     *
     * <pre>
     *竞技场
     * </pre>
     */
    ArenaPos(2, 3),
    /**
     * <code>GroupFightPos = 4;</code>
     *
     * <pre>
     *帮战
     * </pre>
     */
    GroupFightPos(3, 4),
    ;

    /**
     * <code>GroupSecretPos = 1;</code>
     *
     * <pre>
     *秘境
     * </pre>
     */
    public static final int GroupSecretPos_VALUE = 1;
    /**
     * <code>PeakArenaPos = 2;</code>
     *
     * <pre>
     *巅峰竞技场
     * </pre>
     */
    public static final int PeakArenaPos_VALUE = 2;
    /**
     * <code>ArenaPos = 3;</code>
     *
     * <pre>
     *竞技场
     * </pre>
     */
    public static final int ArenaPos_VALUE = 3;
    /**
     * <code>GroupFightPos = 4;</code>
     *
     * <pre>
     *帮战
     * </pre>
     */
    public static final int GroupFightPos_VALUE = 4;


    public final int getNumber() { return value; }

    public static eBattlePositionType valueOf(int value) {
      switch (value) {
        case 1: return GroupSecretPos;
        case 2: return PeakArenaPos;
        case 3: return ArenaPos;
        case 4: return GroupFightPos;
        default: return null;
      }
    }

    public static com.google.protobuf.Internal.EnumLiteMap<eBattlePositionType>
        internalGetValueMap() {
      return internalValueMap;
    }
    private static com.google.protobuf.Internal.EnumLiteMap<eBattlePositionType>
        internalValueMap =
          new com.google.protobuf.Internal.EnumLiteMap<eBattlePositionType>() {
            public eBattlePositionType findValueByNumber(int number) {
              return eBattlePositionType.valueOf(number);
            }
          };

    public final com.google.protobuf.Descriptors.EnumValueDescriptor
        getValueDescriptor() {
      return getDescriptor().getValues().get(index);
    }
    public final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptorForType() {
      return getDescriptor();
    }
    public static final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptor() {
      return com.rwproto.BattleCommon.getDescriptor().getEnumTypes().get(2);
    }

    private static final eBattlePositionType[] VALUES = values();

    public static eBattlePositionType valueOf(
        com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
      if (desc.getType() != getDescriptor()) {
        throw new java.lang.IllegalArgumentException(
          "EnumValueDescriptor is not for this type.");
      }
      return VALUES[desc.getIndex()];
    }

    private final int index;
    private final int value;

    private eBattlePositionType(int index, int value) {
      this.index = index;
      this.value = value;
    }

    // @@protoc_insertion_point(enum_scope:BattleCommon.eBattlePositionType)
  }

  public interface BattleHeroPositionOrBuilder
      extends com.google.protobuf.MessageOrBuilder {

    // required string heroId = 1;
    /**
     * <code>required string heroId = 1;</code>
     *
     * <pre>
     *英雄Id
     * </pre>
     */
    boolean hasHeroId();
    /**
     * <code>required string heroId = 1;</code>
     *
     * <pre>
     *英雄Id
     * </pre>
     */
    java.lang.String getHeroId();
    /**
     * <code>required string heroId = 1;</code>
     *
     * <pre>
     *英雄Id
     * </pre>
     */
    com.google.protobuf.ByteString
        getHeroIdBytes();

    // required int32 pos = 2;
    /**
     * <code>required int32 pos = 2;</code>
     *
     * <pre>
     *站位
     * </pre>
     */
    boolean hasPos();
    /**
     * <code>required int32 pos = 2;</code>
     *
     * <pre>
     *站位
     * </pre>
     */
    int getPos();
  }
  /**
   * Protobuf type {@code BattleCommon.BattleHeroPosition}
   *
   * <pre>
   *阵容上的英雄站位信息
   * </pre>
   */
  public static final class BattleHeroPosition extends
      com.google.protobuf.GeneratedMessage
      implements BattleHeroPositionOrBuilder {
    // Use BattleHeroPosition.newBuilder() to construct.
    private BattleHeroPosition(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
      super(builder);
      this.unknownFields = builder.getUnknownFields();
    }
    private BattleHeroPosition(boolean noInit) { this.unknownFields = com.google.protobuf.UnknownFieldSet.getDefaultInstance(); }

    private static final BattleHeroPosition defaultInstance;
    public static BattleHeroPosition getDefaultInstance() {
      return defaultInstance;
    }

    public BattleHeroPosition getDefaultInstanceForType() {
      return defaultInstance;
    }

    private final com.google.protobuf.UnknownFieldSet unknownFields;
    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
      return this.unknownFields;
    }
    private BattleHeroPosition(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      initFields();
      int mutable_bitField0_ = 0;
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
            case 10: {
              bitField0_ |= 0x00000001;
              heroId_ = input.readBytes();
              break;
            }
            case 16: {
              bitField0_ |= 0x00000002;
              pos_ = input.readInt32();
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
      return com.rwproto.BattleCommon.internal_static_BattleCommon_BattleHeroPosition_descriptor;
    }

    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.rwproto.BattleCommon.internal_static_BattleCommon_BattleHeroPosition_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.rwproto.BattleCommon.BattleHeroPosition.class, com.rwproto.BattleCommon.BattleHeroPosition.Builder.class);
    }

    public static com.google.protobuf.Parser<BattleHeroPosition> PARSER =
        new com.google.protobuf.AbstractParser<BattleHeroPosition>() {
      public BattleHeroPosition parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new BattleHeroPosition(input, extensionRegistry);
      }
    };

    @java.lang.Override
    public com.google.protobuf.Parser<BattleHeroPosition> getParserForType() {
      return PARSER;
    }

    private int bitField0_;
    // required string heroId = 1;
    public static final int HEROID_FIELD_NUMBER = 1;
    private java.lang.Object heroId_;
    /**
     * <code>required string heroId = 1;</code>
     *
     * <pre>
     *英雄Id
     * </pre>
     */
    public boolean hasHeroId() {
      return ((bitField0_ & 0x00000001) == 0x00000001);
    }
    /**
     * <code>required string heroId = 1;</code>
     *
     * <pre>
     *英雄Id
     * </pre>
     */
    public java.lang.String getHeroId() {
      java.lang.Object ref = heroId_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        if (bs.isValidUtf8()) {
          heroId_ = s;
        }
        return s;
      }
    }
    /**
     * <code>required string heroId = 1;</code>
     *
     * <pre>
     *英雄Id
     * </pre>
     */
    public com.google.protobuf.ByteString
        getHeroIdBytes() {
      java.lang.Object ref = heroId_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        heroId_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    // required int32 pos = 2;
    public static final int POS_FIELD_NUMBER = 2;
    private int pos_;
    /**
     * <code>required int32 pos = 2;</code>
     *
     * <pre>
     *站位
     * </pre>
     */
    public boolean hasPos() {
      return ((bitField0_ & 0x00000002) == 0x00000002);
    }
    /**
     * <code>required int32 pos = 2;</code>
     *
     * <pre>
     *站位
     * </pre>
     */
    public int getPos() {
      return pos_;
    }

    private void initFields() {
      heroId_ = "";
      pos_ = 0;
    }
    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized != -1) return isInitialized == 1;

      if (!hasHeroId()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!hasPos()) {
        memoizedIsInitialized = 0;
        return false;
      }
      memoizedIsInitialized = 1;
      return true;
    }

    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      getSerializedSize();
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        output.writeBytes(1, getHeroIdBytes());
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        output.writeInt32(2, pos_);
      }
      getUnknownFields().writeTo(output);
    }

    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;

      size = 0;
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(1, getHeroIdBytes());
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(2, pos_);
      }
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

    public static com.rwproto.BattleCommon.BattleHeroPosition parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.rwproto.BattleCommon.BattleHeroPosition parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.rwproto.BattleCommon.BattleHeroPosition parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.rwproto.BattleCommon.BattleHeroPosition parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.rwproto.BattleCommon.BattleHeroPosition parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static com.rwproto.BattleCommon.BattleHeroPosition parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }
    public static com.rwproto.BattleCommon.BattleHeroPosition parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input);
    }
    public static com.rwproto.BattleCommon.BattleHeroPosition parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input, extensionRegistry);
    }
    public static com.rwproto.BattleCommon.BattleHeroPosition parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static com.rwproto.BattleCommon.BattleHeroPosition parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }

    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(com.rwproto.BattleCommon.BattleHeroPosition prototype) {
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
     * Protobuf type {@code BattleCommon.BattleHeroPosition}
     *
     * <pre>
     *阵容上的英雄站位信息
     * </pre>
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder>
       implements com.rwproto.BattleCommon.BattleHeroPositionOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return com.rwproto.BattleCommon.internal_static_BattleCommon_BattleHeroPosition_descriptor;
      }

      protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return com.rwproto.BattleCommon.internal_static_BattleCommon_BattleHeroPosition_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                com.rwproto.BattleCommon.BattleHeroPosition.class, com.rwproto.BattleCommon.BattleHeroPosition.Builder.class);
      }

      // Construct using com.rwproto.BattleCommon.BattleHeroPosition.newBuilder()
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
        heroId_ = "";
        bitField0_ = (bitField0_ & ~0x00000001);
        pos_ = 0;
        bitField0_ = (bitField0_ & ~0x00000002);
        return this;
      }

      public Builder clone() {
        return create().mergeFrom(buildPartial());
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return com.rwproto.BattleCommon.internal_static_BattleCommon_BattleHeroPosition_descriptor;
      }

      public com.rwproto.BattleCommon.BattleHeroPosition getDefaultInstanceForType() {
        return com.rwproto.BattleCommon.BattleHeroPosition.getDefaultInstance();
      }

      public com.rwproto.BattleCommon.BattleHeroPosition build() {
        com.rwproto.BattleCommon.BattleHeroPosition result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public com.rwproto.BattleCommon.BattleHeroPosition buildPartial() {
        com.rwproto.BattleCommon.BattleHeroPosition result = new com.rwproto.BattleCommon.BattleHeroPosition(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
          to_bitField0_ |= 0x00000001;
        }
        result.heroId_ = heroId_;
        if (((from_bitField0_ & 0x00000002) == 0x00000002)) {
          to_bitField0_ |= 0x00000002;
        }
        result.pos_ = pos_;
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }

      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof com.rwproto.BattleCommon.BattleHeroPosition) {
          return mergeFrom((com.rwproto.BattleCommon.BattleHeroPosition)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(com.rwproto.BattleCommon.BattleHeroPosition other) {
        if (other == com.rwproto.BattleCommon.BattleHeroPosition.getDefaultInstance()) return this;
        if (other.hasHeroId()) {
          bitField0_ |= 0x00000001;
          heroId_ = other.heroId_;
          onChanged();
        }
        if (other.hasPos()) {
          setPos(other.getPos());
        }
        this.mergeUnknownFields(other.getUnknownFields());
        return this;
      }

      public final boolean isInitialized() {
        if (!hasHeroId()) {
          
          return false;
        }
        if (!hasPos()) {
          
          return false;
        }
        return true;
      }

      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        com.rwproto.BattleCommon.BattleHeroPosition parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (com.rwproto.BattleCommon.BattleHeroPosition) e.getUnfinishedMessage();
          throw e;
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      // required string heroId = 1;
      private java.lang.Object heroId_ = "";
      /**
       * <code>required string heroId = 1;</code>
       *
       * <pre>
       *英雄Id
       * </pre>
       */
      public boolean hasHeroId() {
        return ((bitField0_ & 0x00000001) == 0x00000001);
      }
      /**
       * <code>required string heroId = 1;</code>
       *
       * <pre>
       *英雄Id
       * </pre>
       */
      public java.lang.String getHeroId() {
        java.lang.Object ref = heroId_;
        if (!(ref instanceof java.lang.String)) {
          java.lang.String s = ((com.google.protobuf.ByteString) ref)
              .toStringUtf8();
          heroId_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>required string heroId = 1;</code>
       *
       * <pre>
       *英雄Id
       * </pre>
       */
      public com.google.protobuf.ByteString
          getHeroIdBytes() {
        java.lang.Object ref = heroId_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          heroId_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>required string heroId = 1;</code>
       *
       * <pre>
       *英雄Id
       * </pre>
       */
      public Builder setHeroId(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000001;
        heroId_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required string heroId = 1;</code>
       *
       * <pre>
       *英雄Id
       * </pre>
       */
      public Builder clearHeroId() {
        bitField0_ = (bitField0_ & ~0x00000001);
        heroId_ = getDefaultInstance().getHeroId();
        onChanged();
        return this;
      }
      /**
       * <code>required string heroId = 1;</code>
       *
       * <pre>
       *英雄Id
       * </pre>
       */
      public Builder setHeroIdBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000001;
        heroId_ = value;
        onChanged();
        return this;
      }

      // required int32 pos = 2;
      private int pos_ ;
      /**
       * <code>required int32 pos = 2;</code>
       *
       * <pre>
       *站位
       * </pre>
       */
      public boolean hasPos() {
        return ((bitField0_ & 0x00000002) == 0x00000002);
      }
      /**
       * <code>required int32 pos = 2;</code>
       *
       * <pre>
       *站位
       * </pre>
       */
      public int getPos() {
        return pos_;
      }
      /**
       * <code>required int32 pos = 2;</code>
       *
       * <pre>
       *站位
       * </pre>
       */
      public Builder setPos(int value) {
        bitField0_ |= 0x00000002;
        pos_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required int32 pos = 2;</code>
       *
       * <pre>
       *站位
       * </pre>
       */
      public Builder clearPos() {
        bitField0_ = (bitField0_ & ~0x00000002);
        pos_ = 0;
        onChanged();
        return this;
      }

      // @@protoc_insertion_point(builder_scope:BattleCommon.BattleHeroPosition)
    }

    static {
      defaultInstance = new BattleHeroPosition(true);
      defaultInstance.initFields();
    }

    // @@protoc_insertion_point(class_scope:BattleCommon.BattleHeroPosition)
  }

  private static com.google.protobuf.Descriptors.Descriptor
    internal_static_BattleCommon_BattleHeroPosition_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_BattleCommon_BattleHeroPosition_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\022BattleCommon.proto\022\014BattleCommon\"1\n\022Ba" +
      "ttleHeroPosition\022\016\n\006heroId\030\001 \002(\t\022\013\n\003pos\030" +
      "\002 \002(\005*\234\001\n\013ePlayerType\022\016\n\nMAINPLAYER\020\000\022\010\n" +
      "\004HIRE\020\001\022\013\n\007MONSTER\020\002\022\025\n\021MONSTER_TINY_BOS" +
      "S\020\003\022\026\n\022MONSTER_LARGE_BOSS\020\004\022\021\n\rINVISIBLE" +
      "_NPC\020\005\022\021\n\rSUMMON_NORMAL\020\006\022\021\n\rSUMMON_ANIM" +
      "AL\020\007*+\n\013ePlayerCamp\022\006\n\002Me\020\000\022\t\n\005Enemy\020\001\022\t" +
      "\n\005Other\020\002*\\\n\023eBattlePositionType\022\022\n\016Grou" +
      "pSecretPos\020\001\022\020\n\014PeakArenaPos\020\002\022\014\n\010ArenaP" +
      "os\020\003\022\021\n\rGroupFightPos\020\004B\033\n\013com.rwprotoB\014",
      "BattleCommon"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
      new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
        public com.google.protobuf.ExtensionRegistry assignDescriptors(
            com.google.protobuf.Descriptors.FileDescriptor root) {
          descriptor = root;
          internal_static_BattleCommon_BattleHeroPosition_descriptor =
            getDescriptor().getMessageTypes().get(0);
          internal_static_BattleCommon_BattleHeroPosition_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessage.FieldAccessorTable(
              internal_static_BattleCommon_BattleHeroPosition_descriptor,
              new java.lang.String[] { "HeroId", "Pos", });
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