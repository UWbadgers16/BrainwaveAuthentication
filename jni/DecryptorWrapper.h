/*
 * DecryptorWrapper.h
 *
 *  Created on: Feb 8, 2015
 *      Author: John
 */

/*
#ifdef __cplusplus
extern "C" {
#endif
*/

#include <jni.h>
#include <libdecryptor.h>

	class DecryptorWrapper {
	public:
		JNIEXPORT void JNICALL Java_com_example_brainwaveauthentication_DecryptorLibrary_jniActivate(JNIEnv *env, jobject thiz);
};

/*
#ifdef __cplusplus
}
#endif
*/

