#ifndef __net_sourceforge_jnipp_FieldNotFoundException_H
#define __net_sourceforge_jnipp_FieldNotFoundException_H

#include "BaseException.h"

namespace net
{
	namespace sourceforge
	{
		namespace jnipp
		{
			class FieldNotFoundException : public BaseException
			{
			public:
				FieldNotFoundException(const std::string& fieldName)
					: BaseException( "Could not find field " + fieldName )
				{
				}
			};
		}
	}
}

#endif
