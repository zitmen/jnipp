#ifndef __net_sourceforge_jnipp_JDoubleArrayHelper_H
#define __net_sourceforge_jnipp_JDoubleArrayHelper_H

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
			class ComputeDoubleElementType
			{
			public:
				static inline std::string getElementTypeString()
				{
					return "[" + ComputeDoubleElementType<dims - 1>::getElementTypeString();
				}
			};
			
			class ComputeDoubleElementType<1>
			{
			public:
				static inline std::string getElementTypeString()
				{
					return "I";
				}
			};

			template<unsigned int dims>
			class JDoubleArrayHelper
			{
			private:
				jobjectArray nativeArray;
				jsize length;

			public:
				JDoubleArrayHelper()
					: nativeArray( NULL ), length( 0 )
				{
				}
				
				JDoubleArrayHelper(jsize length)
				{
					this->length = length;
					jclass cls = JNIEnvHelper::FindClass( "" );
					nativeArray = JNIEnvHelper::NewObjectArray( length, cls, NULL );
				}
				
				JDoubleArrayHelper(jobject nativeArray)
				{
					this->nativeArray = reinterpret_cast<jobjectArray>( nativeArray );
					length = JNIEnvHelper::GetArrayLength( this->nativeArray );
				}
				
				JDoubleArrayHelper(JDoubleArrayHelper<dims>& rhs)
				{
					*this = rhs;
				}
				
				JDoubleArrayHelper<dims>& operator=(JDoubleArrayHelper<dims>& rhs)
				{
					this->nativeArray = rhs.nativeArray;
					this->length = rhs.length;
					return *this;
				}
				
				~JDoubleArrayHelper()
				{
				}
				
				JDoubleArrayHelper<dims - 1> getElementAt(jsize index) const
				{
					if ( index >= length )
						throw ArrayIndexOutOfBoundsException( index );

					return JNIEnvHelper::GetObjectArrayElement( nativeArray, index );
				}

				void setElementAt(jsize index, JDoubleArrayHelper<dims - 1>& value)
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
			
			class JDoubleArrayHelper<1>
			{
			private:
				jsize length;
				jdoubleArray nativeArray;
				jdouble* elems;
				bool dirty;
			
			public:
				JDoubleArrayHelper()
					: length( 0 ), nativeArray( NULL ), elems( NULL ), dirty( false )
				{
				}
				
				JDoubleArrayHelper(jsize length)
				{
					this->length = length;
					nativeArray = JNIEnvHelper::NewDoubleArray( length );
					elems = JNIEnvHelper::GetDoubleArrayElements( nativeArray, NULL );
					dirty = false;
				}
				
				JDoubleArrayHelper(jobject nativeArray)
				{
					this->nativeArray = reinterpret_cast<jdoubleArray>( nativeArray );
					length = JNIEnvHelper::GetArrayLength( this->nativeArray );
					elems = JNIEnvHelper::GetDoubleArrayElements( this->nativeArray, NULL );
					dirty = false;
				}
				
				JDoubleArrayHelper(JDoubleArrayHelper<1>& rhs)
				{
					*this = rhs;
				}
				
				JDoubleArrayHelper<1>& operator=(JDoubleArrayHelper<1>& rhs)
				{
					this->dirty = rhs.dirty;
					this->nativeArray = rhs.nativeArray;
					this->length = rhs.length;
					this->elems = rhs.elems;
					return *this;
				}
				
				~JDoubleArrayHelper()
				{
					if ( elems != NULL )
					{
						JNIEnvHelper::ReleaseDoubleArrayElements( nativeArray, elems, (dirty == true ? 0 : JNI_ABORT) );
						elems = NULL;
					}
				}
				
				jdouble getElementAt(jsize index) const
				{
					if ( index >= length )
						throw ArrayIndexOutOfBoundsException( index );

					return elems[index];
				}

				void setElementAt(jsize index, jdouble value)
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
				
				operator jdoubleArray()
				{
					if ( elems != NULL )
					{
						JNIEnvHelper::ReleaseDoubleArrayElements( nativeArray, elems, (dirty == true ? 0 : JNI_ABORT) );
						elems = NULL;
					}
					return nativeArray;
				}
				
				operator jobject()
				{
					if ( elems != NULL )
					{
						JNIEnvHelper::ReleaseDoubleArrayElements( nativeArray, elems, (dirty == true ? 0 : JNI_ABORT) );
						elems = NULL;
					}
					return nativeArray;
				}
			};
		}
	}
}

#endif
