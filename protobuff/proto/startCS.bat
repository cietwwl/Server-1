
@echo ----begin to explain cshape file----

for %%i in (DataSyn.proto, MsgDef.proto) do (..\cs\protogen -p:detectMissing -i:%%i -o:%csPath%%%~ni.cs)

@echo --------------end--------------