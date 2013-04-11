#ifndef __net_sourceforge_jnipp_ProxyArray_H
#define __net_sourceforge_jnipp_ProxyArray_H

#ifdef WIN32
#pragma warning(disable:4251)
#pragma warning(disable:4786)
#endif

#include <string>
#include "JNIEnvHelper.h"
#include "ArrayIndexOutOfBoundsException.h"

namespace net
{
	namespace sourceforge
	{
		namespace jnipp
		{
#ifdef __NoPartialSpec
			template<class T>
			class PA
			{
			private:
#endif
				template<unsigned int dims>
				class ComputeElementType
				{
				public:
					static inline std::string getElementTypeString()
					{
						return "[" + ComputeElementType<dims - 1>::getElementTypeString();
					}
				};

				template<>
				class ComputeElementType<1>
				{
				public:
					static inline std::string getElementTypeString()
					{
						return "java/lang/Object;";
					}
				};

#ifdef __NoPartialSpec
			public:
				template<unsigned int dims>
#else
				template<class T, unsigned int dims>
#endif
				class ProxyArray
				{
				private:
					jobjectArray nativeArray;
					jsize length;

				public:
					ProxyArray()
						: nativeArray( NULL ), length( 0 )
					{
					}
					
					ProxyArray(jsize length)
					{
						this->length = length;
						jclass cls = JNIEnvHelper::FindClass( ComputeElementType<dims>::getElementTypeString().c_str() );
						nativeArray = JNIEnvHelper::NewObjectArray( length, cls, NULL );
					}
					
					ProxyArray(const jobject nativeArray)
					{
						this->nativeArray = reinterpret_cast<jobjectArray>( nativeArray );
						length = JNIEnvHelper::GetArrayLength( this->nativeArray );
					}

#ifdef __NoPartialSpec
					ProxyArray(const ProxyArray<dims>& rhs)
#else
					ProxyArray(const ProxyArray<T, dims>& rhs)
#endif
					{
						*this = rhs;
					}

#ifdef __NoPartialSpec
					ProxyArray<dims>& operator=(const ProxyArray<dims>& rhs)
#else
					ProxyArray<T, dims>& operator=(const ProxyArray<T, dims>& rhs)
#endif
					{
						this->nativeArray = rhs.nativeArray;
						this->length = rhs.length;
						return *this;
					}
					
					~ProxyArray()
					{
					}

#ifdef __NoPartialSpec
					ProxyArray<dims - 1> getElementAt(jsize index) const
#else
					ProxyArray<T, dims - 1> getElementAt(jsize index) const
#endif
					{
						if ( index >= length )
							throw ArrayIndexOutOfBoundsException( index );

						return JNIEnvHelper::GetObjectArrayElement( nativeArray, index );
					}

#ifdef __NoPartialSpec
					void setElementAt(jsize index, const ProxyArray<dims - 1>& value)
#else
					void setElementAt(jsize index, const ProxyArray<T, dims - 1>& value)
#endif
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
				
#ifdef __NoPartialSpec
				class ProxyArray<1>
#else
				template<class T>
				class ProxyArray<T, 1>
#endif
				{
				private:
					jobjectArray nativeArray;
					jsize length;
				
				public:
#ifdef __NoPartialSpec
					ProxyArray<1>()
#else
					ProxyArray<T, 1>()
#endif
						: nativeArray( NULL ), length( 0 )
					{
					}
					
#ifdef __NoPartialSpec
					ProxyArray<1>(jsize length)
#else
					ProxyArray<T, 1>(jsize length)
#endif
					{
						this->length = length;
						jclass cls = T::_getObjectClass();
						nativeArray = JNIEnvHelper::NewObjectArray( length, cls, NULL );
					}
					
#ifdef __NoPartialSpec
					ProxyArray<1>(const jobject nativeArray)
#else
					ProxyArray<T, 1>(const jobject nativeArray)
#endif
					{
						this->nativeArray = reinterpret_cast<jobjectArray>( nativeArray );
						length = JNIEnvHelper::GetArrayLength( this->nativeArray );
					}
					
#ifdef __NoPartialSpec
					ProxyArray<1>(const ProxyArray<1>& rhs)
#else
					ProxyArray<T, 1>(const ProxyArray<T, 1>& rhs)
#endif
					{
						*this = rhs;
					}
					
#ifdef __NoPartialSpec
					ProxyArray<1>& operator=(const ProxyArray<1>& rhs)
#else
					ProxyArray<T, 1>& operator=(const ProxyArray<T, 1>& rhs)
#endif
					{
						this->nativeArray = rhs.nativeArray;
						this->length = rhs.length;
						return *this;
					}
					
#ifdef __NoPartialSpec
					~ProxyArray<1>()
#else
					~ProxyArray<T, 1>()
#endif
					{
					}
					
					T& getElementAt(jsize index) const
					{
						if ( index >= length )
							throw ArrayIndexOutOfBoundsException( index );

						return JNIEnvHelper::GetObjectArrayElement( nativeArray, index );
					}

					void setElementAt(jsize index, const T& value)
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
#ifdef __NoPartialSpec
			};
#endif
		}
	}
}

#endif
