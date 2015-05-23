#ifndef LIBDECRYPTOR_H
#define LIBDECRYPTOR_H

/*
#ifdef __cplusplus
extern "C" {
#endif
*/

#include "Rijndael.h"

class LibDecryptor {
public:
    LibDecryptor();
    long Activate(long in, char* USBserialNumber);
    bool unscramble(char* in);
private:
    bool activeFlag;
    char serialNumber[16];
    bool firstcall;
    long UID;
    long lastactive;
    long mcrv(long in);
    CRijndael* oRijndael;
};

#endif // LIBDECRYPTOR_H

/*
#ifdef __cplusplus
}
#endif
*/
