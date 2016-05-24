
@echo ----begin to explain java file----

for %%i in (CommonMsg.proto,ItemBag.proto) do (..\java\protoc --java_out=%javaPath% %%i)

@echo --------------end--------------

