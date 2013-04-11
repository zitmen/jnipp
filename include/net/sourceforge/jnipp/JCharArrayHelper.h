#ifndef __net_sourceforge_jnipp_JCharArrayHelper_H
#define __net_sourceforge_jnipp_JCharArrayHelper_H

#ifdef WIN32
#pragma warning(disable:4251)
#pragma warning(disable:4786)
#endif

#include <jni.h>
#include <string>
#include "JNIEnvHelper.h"
#include "ArrayIndexOutOfBoundsException.h"

namespace net
{
	namespace sourceforge
	{
		namespace jnipp
		{
			template<unsigned int dims>
			class ComputeCharElementType
			{
			public:
				static inline std::string getElementTypeString()
				{
					return "[" + ComputeCharElementType<dims - 1>::getElementTypeString();
				}
			};
			
			class ComputeCharElementType<1>
			{
			public:
				static inline std::string getElementTypeString()
				{
					return "I";
				}
			};

			template<unsigned int dims>
			class JCharArrayHelper
			{
			private:
				jobjectArray nativeArray;
				jsize length;

			public:
				JCharArrayHelper()
					: nativeArray( NULL ), length( 0 )
				{
				}
				
				JCharArrayHelper(jsize length)
				{
					this->length = length;
					jclass cls = JNIEnvHelper::FindClass( ComputeCharElementType<dims>::getElementTypeString().c_str() );
					nativeArray = JNIEnvHelper::NewObjectArray( length, cls, NULL );
				}
				
				JCharArrayHelper(jobject nativeArray)
				{
					this->nativeArray = reinterpret_cast<jobjectArray>( nativeArray );
					length = JNIEnvHelper::GetArrayLength( this->nativeArray );
				}
				
				JCharArrayHelper(JCharArrayHelper<dims>& rhs)
				{
					*this = rhs;
				}
				
				JCharArrayHelper<dims>& operator=(JCharArrayHelper<dims>& rhs)
				{
					this->nativeArray = rhs.nativeArray;
					this->length = rhs.length;
					return *this;
				}
				
				~JCharArrayHelper()
				{
				}
				
				JCharArrayHelper<dims - 1> getElementAt(jsize index) const
				{
					if ( index >= length )
						throw ArrayIndexOutOfBoundsException( index );

					return JNIEnvHelper::GetObjectArrayElement( nativeArray, index );
				}

				void setElementAt(jsize index, JCharArrayHelper<dims - 1>& value)
				{
					if ( index >= length )
						throw ArrayIndexOutOfBoundsException( index );

					JNIEnvHelper::SetObjectArrayElement( nativeArray, index, static_cast<jobject>(value) );
				}

				jsize getLength() const
				{
					return length;
				}
				
				operator jobjectArray() const
				{
					return nativeArray;
				}
				
				operator jobject() const
				{
					return nativeArray;
				}
			};
			
			class JCharArrayHelper<1>
			{
			private:
				jsize length;
				jcharArray nativeArray;
				jchar* elems;
				bool dirty;
			
			public:
				JCharArrayHelper()
					: length( 0 ), nativeArray( NULL ), elems( NULL ), dirty( false )
				{
				}
				
				JCharArrayHelper(jsize length)
				{
					this->length = length;
					nativeArray = JNIEnvHelper::NewCharArray( length );
					elems = JNIEnvHelper::GetCharArrayElements( nativeArray, NULL );
					dirty = false;
				}
				
				JCharArrayHelper(jobject nativeArray)
				{
					this->nativeArray = reinterpret_cast<jcharArray>( nativeArray );
					length = JNIEnvHelper::GetArrayLength( this->nativeArray );
					elems = JNIEnvHelper::GetCharArrayElements( this->nativeArray, NULL );
					dirty = false;
				}
				
				JCharArrayHelper(JCharArrayHelper<1>& rhs)
				{
					*this = rhs;
				}
				
				JCharArrayHelper<1>& operator=(JCharArrayHelper<1>& rhs)
				{
					this->dirty = rhs.dirty;
					this->nativeArray = rhs.nativeArray;
					this->length = rhs.length;
					this->elems = rhs.elems;
					return *this;
				}
				
				~JCharArrayHelper()
				{
					if ( elems != NULL )
					{
						JNIEnvHelper::ReleaseCharArrayElements( nativeArray, elems, (dirty == true ? 0 : JNI_ABORT) );
						elems = NULL;
					}
				}
				
				jchar getElementAt(jsize index) const
				{
					if ( index >= length )
						throw ArrayIndexOutOfBoundsException( index );

					return elems[index];
				}

				void setElementAt(jsize index, jchar value)
				{
					if ( index >= length )
						throw ArrayIndexOutOfBoundsException( index );

					elems[index] = value;
					dirty = true;
				}

				jsize getLength() const
				{
					return length;
				}
				
				operator jcharArray()
				{
					if ( elems != NULL )
					{
						JNIEnvHelper::ReleaseCharArrayElements( nativeArray, elems, (dirty == true ? 0 : JNI_ABORT) );
						elems = NULL;
					}
					return nativeArray;
				}
				
				operator jobject()
				{
					if ( elems != NULL )
					{
						JNIEnvHelper::ReleaseCharArrayElements( nativeArray, elems, (dirty == true ? 0 : JNI_ABORT) );
						elems = NULL;
					}
					return nativeArray;
				}
			};
		}
	}
}

#endif
