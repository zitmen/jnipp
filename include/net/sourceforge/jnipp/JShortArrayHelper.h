#ifndef __net_sourceforge_jnipp_JShortArrayHelper_H
#define __net_sourceforge_jnipp_JShortArrayHelper_H

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
			class ComputeShortElementType
			{
			public:
				static inline std::string getElementTypeString()
				{
					return "[" + ComputeShortElementType<dims - 1>::getElementTypeString();
				}
			};
			
			class ComputeShortElementType<1>
			{
			public:
				static inline std::string getElementTypeString()
				{
					return "I";
				}
			};

			template<unsigned int dims>
			class JShortArrayHelper
			{
			private:
				jobjectArray nativeArray;
				jsize length;

			public:
				JShortArrayHelper()
					: nativeArray( NULL ), length( 0 )
				{
				}
				
				JShortArrayHelper(jsize length)
				{
					this->length = length;
					jclass cls = JNIEnvHelper::FindClass( ComputeShortElementType<dims>::getElementTypeString().c_str() );
					nativeArray = JNIEnvHelper::NewObjectArray( length, cls, NULL );
				}
				
				JShortArrayHelper(jobject nativeArray)
				{
					this->nativeArray = reinterpret_cast<jobjectArray>( nativeArray );
					length = JNIEnvHelper::GetArrayLength( this->nativeArray );
				}
				
				JShortArrayHelper(JShortArrayHelper<dims>& rhs)
				{
					*this = rhs;
				}
				
				JShortArrayHelper<dims>& operator=(JShortArrayHelper<dims>& rhs)
				{
					this->nativeArray = rhs.nativeArray;
					this->length = rhs.length;
					return *this;
				}
				
				~JShortArrayHelper()
				{
				}
				
				JShortArrayHelper<dims - 1> getElementAt(jsize index) const
				{
					if ( index >= length )
						throw ArrayIndexOutOfBoundsException( index );

					return JNIEnvHelper::GetObjectArrayElement( nativeArray, index );
				}

				void setElementAt(jsize index, JShortArrayHelper<dims - 1>& value)
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
			
			class JShortArrayHelper<1>
			{
			private:
				jsize length;
				jshortArray nativeArray;
				jshort* elems;
				bool dirty;
			
			public:
				JShortArrayHelper()
					: length( 0 ), nativeArray( NULL ), elems( NULL ), dirty( false )
				{
				}
				
				JShortArrayHelper(jsize length)
				{
					this->length = length;
					nativeArray = JNIEnvHelper::NewShortArray( length );
					elems = JNIEnvHelper::GetShortArrayElements( nativeArray, NULL );
					dirty = false;
				}
				
				JShortArrayHelper(jobject nativeArray)
				{
					this->nativeArray = reinterpret_cast<jshortArray>( nativeArray );
					length = JNIEnvHelper::GetArrayLength( this->nativeArray );
					elems = JNIEnvHelper::GetShortArrayElements( this->nativeArray, NULL );
					dirty = false;
				}
				
				JShortArrayHelper(JShortArrayHelper<1>& rhs)
				{
					*this = rhs;
				}
				
				JShortArrayHelper<1>& operator=(JShortArrayHelper<1>& rhs)
				{
					this->dirty = rhs.dirty;
					this->nativeArray = rhs.nativeArray;
					this->length = rhs.length;
					this->elems = rhs.elems;
					return *this;
				}
				
				~JShortArrayHelper()
				{
					if ( elems != NULL )
					{
						JNIEnvHelper::ReleaseShortArrayElements( nativeArray, elems, (dirty == true ? 0 : JNI_ABORT) );
						elems = NULL;
					}
				}
				
				jshort getElementAt(jsize index) const
				{
					if ( index >= length )
						throw ArrayIndexOutOfBoundsException( index );

					return elems[index];
				}

				void setElementAt(jsize index, jshort value)
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
				
				operator jshortArray()
				{
					if ( elems != NULL )
					{
						JNIEnvHelper::ReleaseShortArrayElements( nativeArray, elems, (dirty == true ? 0 : JNI_ABORT) );
						elems = NULL;
					}
					return nativeArray;
				}
				
				operator jobject()
				{
					if ( elems != NULL )
					{
						JNIEnvHelper::ReleaseShortArrayElements( nativeArray, elems, (dirty == true ? 0 : JNI_ABORT) );
						elems = NULL;
					}
					return nativeArray;
				}
			};
		}
	}
}

#endif
