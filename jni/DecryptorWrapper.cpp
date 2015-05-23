/*
 * DecryptorWrapper.cpp
 *
 *  Created on: Feb 8, 2015
 *      Author: John
 */

/*
#ifdef __cplusplus
extern "C" {
#endif
*/

#include <string.h>
#include <jni.h>
#include <libdecryptor.h>
#include "DecryptorWrapper.h"

JNIEXPORT void JNICALL
DecryptorWrapper::Java_com_example_brainwaveauthentication_DecryptorLibrary_jniActivate(JNIEnv *env, jobject thiz)
{
	LibDecryptor* decryptor;
	char serialNumber[] = "20130201000835";
	decryptor->Activate(1, serialNumber);
}

/*
#ifdef __cplusplus
}
#endif
*/
