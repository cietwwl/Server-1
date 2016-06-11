
@echo ----begin to explain java file----

for %%i in (DataSyn.proto) do (..\java\protoc --java_out=%javaPath% %%i)

@echo --------------end--------------

