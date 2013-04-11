#ifndef __net_sourceforge_jnipp_JByteArrayHelper_H
#define __net_sourceforge_jnipp_JByteArrayHelper_H

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
			class ComputeByteElementType
			{
			public:
				static inline std::string getElementTypeString()
				{
					return "[" + ComputeByteElementType<dims - 1>::getElementTypeString();
				}
			};
			
			class ComputeByteElementType<1>
			{
			public:
				static inline std::string getElementTypeString()
				{
					return "I";
				}
			};

			template<unsigned int dims>
			class JByteArrayHelper
			{
			private:
				jobjectArray nativeArray;
				jsize length;

			public:
				JByteArrayHelper()
					: nativeArray( NULL ), length( 0 )
				{
				}
				
				JByteArrayHelper(jsize length)
				{
					this->length = length;
					jclass cls = JNIEnvHelper::FindClass( ComputeByteElementType<dims>::getElementTypeString().c_str() );
					nativeArray = JNIEnvHelper::NewObjectArray( length, cls, NULL );
				}
				
				JByteArrayHelper(jobject nativeArray)
				{
					this->nativeArray = reinterpret_cast<jobjectArray>( nativeArray );
					length = JNIEnvHelper::GetArrayLength( this->nativeArray );
				}
				
				JByteArrayHelper(JByteArrayHelper<dims>& rhs)
				{
					*this = rhs;
				}
				
				JByteArrayHelper<dims>& operator=(JByteArrayHelper<dims>& rhs)
				{
					this->nativeArray = rhs.nativeArray;
					this->length = rhs.length;
					return *this;
				}
				
				~JByteArrayHelper()
				{
				}
				
				JByteArrayHelper<dims - 1> getElementAt(jsize index) const
				{
					if ( index >= length )
						throw ArrayIndexOutOfBoundsException( index );

					return JNIEnvHelper::GetObjectArrayElement( nativeArray, index );
				}

				void setElementAt(jsize index, JByteArrayHelper<dims - 1>& value)
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
			
			class JByteArrayHelper<1>
			{
			private:
				jsize length;
				jbyteArray nativeArray;
				jbyte* elems;
				bool dirty;
			
			public:
				JByteArrayHelper()
					: length( 0 ), nativeArray( NULL ), elems( NULL ), dirty( false )
				{
				}
				
				JByteArrayHelper(jsize length)
				{
					this->length = length;
					nativeArray = JNIEnvHelper::NewByteArray( length );
					elems = JNIEnvHelper::GetByteArrayElements( nativeArray, NULL );
					dirty = false;
				}
				
				JByteArrayHelper(jobject nativeArray)
				{
					this->nativeArray = reinterpret_cast<jbyteArray>( nativeArray );
					length = JNIEnvHelper::GetArrayLength( this->nativeArray );
					elems = JNIEnvHelper::GetByteArrayElements( this->nativeArray, NULL );
					dirty = false;
				}
				
				JByteArrayHelper(JByteArrayHelper<1>& rhs)
				{
					*this = rhs;
				}
				
				JByteArrayHelper<1>& operator=(JByteArrayHelper<1>& rhs)
				{
					this->dirty = rhs.dirty;
					this->nativeArray = rhs.nativeArray;
					this->length = rhs.length;
					this->elems = rhs.elems;
					return *this;
				}
				
				~JByteArrayHelper()
				{
					if ( elems != NULL )
					{
						JNIEnvHelper::ReleaseByteArrayElements( nativeArray, elems, (dirty == true ? 0 : JNI_ABORT) );
						elems = NULL;
					}
				}
				
				jbyte getElementAt(jsize index) const
				{
					if ( index >= length )
						throw ArrayIndexOutOfBoundsException( index );

					return elems[index];
				}

				void setElementAt(jsize index, jbyte value)
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
				
				operator jbyteArray()
				{
					if ( elems != NULL )
					{
						JNIEnvHelper::ReleaseByteArrayElements( nativeArray, elems, (dirty == true ? 0 : JNI_ABORT) );
						elems = NULL;
					}
					return nativeArray;
				}
				
				operator jobject()
				{
					if ( elems != NULL )
					{
						JNIEnvHelper::ReleaseByteArrayElements( nativeArray, elems, (dirty == true ? 0 : JNI_ABORT) );
						elems = NULL;
					}
					return nativeArray;
				}
			};
		}
	}
}

#endif
