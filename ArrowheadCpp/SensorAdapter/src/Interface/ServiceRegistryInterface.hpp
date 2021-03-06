#pragma once

using namespace std;

#include <stdio.h>
#include <stdlib.h>
#include <string>
#include <cstring>
#include <list>

#include "json.hpp"
#include "Http_Handler.hpp"
#include "Https_Handler.hpp"

#ifdef __linux__
     #include "ini/iniparser.h"
#elif _WIN32
     extern "C" {
     #include "ini/iniparser.h"
     }
#endif

using namespace std;
using namespace nlohmann;


typedef struct _Arrowhead_Data_ext
{
	string 				sServiceDefinition;
	string 				sserviceInterface;
	string				sSystemName;
	string				sServiceURI;
	map<string, string>		vService_Meta;
	string				sAuthenticationInfo;
} Arrowhead_Data_ext;

class ServiceRegistryInterface :
    Http_Handler,
    Https_Handler
{
private:
	dictionary  *pini = NULL;

	string SR_BASE_URI;
	string SR_BASE_URI_HTTPS;
	string ADDRESS;
	string ADDRESS6;
	unsigned short PORT;
	string URI;
	string HTTPsURI;

	dictionary *Load_IniFile(char *fname);
	int Unload_IniFile();

public:
	ServiceRegistryInterface(string ini_file);
	ServiceRegistryInterface();
	~ServiceRegistryInterface();

	bool init_ServiceRegistryInterface( string ini_file );
	bool init_ServiceRegistryInterface( string _sHttpUri, string _sHttpsUri, string _sAddr, string _sAddr6, uint16_t _uPort );

	int deinit( );
	int registerToServiceRegistry(Arrowhead_Data_ext &stAH_data, bool _bSecure);
	int unregisterFromServiceRegistry(Arrowhead_Data_ext &stAH_data, bool _bSecureArrowheadInterface);

	int httpGETCallback(const char *Id, string *pData_str);
	int httpsGETCallback(const char *Id, string *pData_str, string param_token, string param_signature, string clientDistName);

     int httpPOSTCallback(const char *url, const char *payload);
     int httpsPOSTCallback(const char *url, const char *payload);

	virtual int Callback_Serve_HTTP_GET(const char *Id, string *pData_str);
	virtual int Callback_Serve_HTTPs_GET(const char *Id, string *pData_str, string param_token, string param_signature, string clientDistName);

     virtual int Callback_Serve_HTTP_POST(const char *url, const char *payload);
     virtual int Callback_Serve_HTTPs_POST(const char *url, const char *payload);
};
