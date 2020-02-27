// NahonUSBLib.cpp : Defines the entry point for the DLL application.
//


#include "USBLibJava.h"
#include "USB_Driver.h"
#include "windows.h"


#pragma comment(lib, "USB_Driver.lib") 


JNIEXPORT jint JNICALL Java_USBDriver_USBLib_USBScanDevImpl
  (JNIEnv *, jclass, jint NeedInit)
{
	return USBScanDev(NeedInit);
}

/*
 * Class:     USBDriver_USBLib
 * Method:    OpenUSBImpl
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_USBDriver_USBLib_OpenUSBImpl
  (JNIEnv *, jclass, jint DevIndex)
{
	return USBOpenDev(DevIndex);
}


/*
 * Class:     USBDriver_USBLib
 * Method:    USBBulkWriteDataImpl
 * Signature: (II[BII)I
 */
JNIEXPORT jint JNICALL Java_USBDriver_USBLib_USBBulkWriteDataImpl
  (JNIEnv * env, jclass cl, jint nBoardID, jbyteArray sendBuffer, jint len, jint waittime)
{
	jbyte *tmp = (env)->GetByteArrayElements(sendBuffer, 0); 
	jint ret = USBBulkWriteData(nBoardID, EP2_OUT,(char *)tmp,len, waittime);
	(env)-> ReleaseByteArrayElements(sendBuffer, tmp, 0);
	return ret;
}

/*
 * Class:     USBDriver_USBLib
 * Method:    USBBulkReadDataImpl
 * Signature: (II[BII)I
 */
JNIEXPORT jint JNICALL Java_USBDriver_USBLib_USBBulkReadDataImpl
  (JNIEnv * env, jclass cl, jint nBoardID, jbyteArray recBuffer, jint len, jint waittime)
{
	jbyte *tmp= (env)->GetByteArrayElements(recBuffer,0); 
	int rclen = USBBulkReadData(nBoardID, EP2_IN,(char *)tmp,len, waittime);
	if(rclen > 0)
	{	
		(env)->SetByteArrayRegion(recBuffer,0, rclen, tmp); 
		(env)-> ReleaseByteArrayElements(recBuffer, tmp, 0);
		return rclen;
	}
	else
	{
		(env)-> ReleaseByteArrayElements(recBuffer, tmp, 0);
		return 0;
	}
}

/*
 * Class:     USBDriver_USBLib
 * Method:    USBCtrlDataImpl
 * Signature: (IIIII[BII)I
 */
JNIEXPORT jint JNICALL Java_USBDriver_USBLib_USBCtrlDataImpl
  (JNIEnv * env, jclass cl, jint nBoardID, jint requesttype, jint request, jint value, jint index, jbyteArray reqBuffer, jint size, jint waittime)
{
	unsigned char *tmp = (unsigned char *)(env)->GetByteArrayElements(reqBuffer,0); 
	jint ret =  USBCtrlData( nBoardID, requesttype, request, value,  index, (char *)tmp, size,waittime);
	(env)-> ReleaseByteArrayElements(reqBuffer, (jbyte *)tmp, 0);
	return ret;
}

/*
 * Class:     USBDriver_USBLib
 * Method:    CloseUSBImpl
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_USBDriver_USBLib_CloseUSBImpl
  (JNIEnv *, jclass, jint DevIndex)
{
	return USBCloseDev(DevIndex);
}
