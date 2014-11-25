#ifndef LIBDECRYPTOR_H
#define LIBDECRYPTOR_H

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
    CRijndael::CRijndael* oRijndael;
};

#endif // LIBDECRYPTOR_H
