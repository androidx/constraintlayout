#include <jni.h>
#include <string>
#include "lua.hpp"


extern "C" JNIEXPORT jstring JNICALL
Java_androidx_demo_lua_NativeLib_stringFromJNI(
        JNIEnv* env,
        jobject /* this */,
        jstring jEval,
        jstring jAnswer) {
    std::string hello = "Hello from lua ?";
    std::string code = "";

    lua_State *L = luaL_newstate();
    luaL_openlibs(L);
    const char *eval = env->GetStringUTFChars(jEval, 0);
    const char *answer= env->GetStringUTFChars(jAnswer, 0);

    if (luaL_loadstring(L, eval) == LUA_OK) {
        if (lua_pcall(L, 0, 0, 0) == LUA_OK) {
            lua_pop(L, lua_gettop(L));
        } else {
            hello = " lua lua pcall";
        }
        lua_getglobal(L, answer);

            const char * message = lua_tostring(L, -1);
            lua_pop(L, 1);
            hello = message;

    } else {
        hello = " lua load string error";
    }

    lua_close(L);
    return env->NewStringUTF(hello.c_str());

}
